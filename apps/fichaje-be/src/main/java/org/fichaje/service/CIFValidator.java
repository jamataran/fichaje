package org.fichaje.service;

import org.springframework.stereotype.Component;

@Component
public class CIFValidator {

    public boolean isValid(String cif) {
        if (cif == null || cif.isBlank()) {
            return false;
        }

        String cifUpper = cif.toUpperCase().trim();

        if (!cifUpper.matches("^[ABCDEFGHJKLMNPQRSUVW][0-9]{7}[0-9A-J]$")) {
            return false;
        }

        int sumaPares = 0;
        int sumaImpares = 0;

        for (int i = 1; i < 8; i++) {
            int digit = cifUpper.charAt(i) - '0';

            if (i % 2 == 0) {
                sumaPares += digit;
            } else {
                int doble = digit * 2;
                sumaImpares += doble > 9 ? doble - 9 : doble;
            }
        }

        int suma = (sumaPares + sumaImpares) % 10;
        int controlValue = suma == 0 ? 0 : 10 - suma;

        char firstChar = cifUpper.charAt(0);
        char lastChar = cifUpper.charAt(8);
        char expectedNumber = Character.forDigit(controlValue, 10);
        char expectedLetter = "JABCDEFGHI".charAt(controlValue);

        // Validación estricta del tipo de carácter de control según la letra inicial
        if ("ABEH".indexOf(firstChar) != -1) {
            return lastChar == expectedNumber;
        } else if ("KPQS".indexOf(firstChar) != -1) {
            return lastChar == expectedLetter;
        } else {
            return lastChar == expectedNumber || lastChar == expectedLetter;
        }
    }
}