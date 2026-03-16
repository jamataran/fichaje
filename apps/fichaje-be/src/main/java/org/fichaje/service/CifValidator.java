package org.fichaje.service;

import org.springframework.stereotype.Component;

@Component
public class CifValidator {
    public boolean isValid(String cif) {
        if (cif == null || !cif.matches("^[ABCDEFGHJKLMNPQRSUVW]{1}[0-9]{7}[0-9A-J]{1}$")) {
            return false;
        }

        String cifUpper = cif.toUpperCase();
        int[] pares = {0, 0, 0, 0};
        int[] impares = {0, 0, 0, 0};
        int sumaPares = 0;
        int sumaImpares = 0;

        for (int i = 1; i < 7; i++) {
            int digit = cifUpper.charAt(i) - '0';
            if (i % 2 == 0) {
                sumaPares += digit;
            } else {
                int doble = digit * 2;
                sumaImpares += doble > 9 ? doble - 9 : doble;
            }
        }

        int suma = (sumaPares + sumaImpares) % 10;
        int control = suma == 0 ? 0 : 10 - suma;

        char lastChar = cifUpper.charAt(8);
        String letrasControl = "JABCDEFGHI";

        return lastChar == Character.forDigit(control, 10) ||
                lastChar == letrasControl.charAt(control);
    }
}
