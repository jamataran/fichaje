package org.fichaje.dto;

import jakarta.validation.constraints.NotBlank;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginUsuario {
    @NotBlank
    private String numero;
    @NotBlank
    private String password;
    private Long empresaId;
    private Long sedeId;
}
