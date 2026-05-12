package org.fichaje.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationExceptions(MethodArgumentNotValidException ex) {
        ProblemDetail problem = ProblemDetail
                .forStatusAndDetail(HttpStatus.BAD_REQUEST, "La petición contiene campos inválidos");

        Map<String, String> errores = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String campo = ((FieldError) error).getField();
            String mensaje = error.getDefaultMessage();
            errores.put(campo, mensaje);
        });

        problem.setProperty("errores", errores);
        return problem;
    }


    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgumentException(IllegalArgumentException ex) {
        return ProblemDetail
                .forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail handleDataIntegrity(DataIntegrityViolationException ex) {
        String mensaje = "Error de integridad de datos en el sistema";
        String causa = ex.getMostSpecificCause().getMessage().toLowerCase();

        if (causa.contains("uk_empresa_cif") || causa.contains("cif")) {
            mensaje = "Ya existe una empresa con ese CIF en el sistema";
        } else if (causa.contains("uk_sede_empresa_nombre") || causa.contains("sede")) {
            mensaje = "Ya existe una sede con ese nombre en esta empresa";
        } else if (causa.contains("uk_empresa_parametros_empresa_clave")) {
            mensaje = "Ya existe un parámetro con esa clave en esta empresa";
        } else if (causa.contains("uk_sede_parametros_sede_clave")) {
            mensaje = "Ya existe un parámetro con esa clave en esta sede";
        } else if (causa.contains("duplicate") || causa.contains("entry")) {
            mensaje = "Ya existe un registro duplicado en el sistema";
        } else if (causa.contains("null") || causa.contains("not-null") || causa.contains("cannot be null")) {
            mensaje = "Faltan campos obligatorios en el registro";
        }

        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, mensaje);
    }

    @ExceptionHandler(BusinessException.class)
    public ProblemDetail handleBusinessException(BusinessException ex) {
        return ProblemDetail
                .forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(EmpresaNotFoundException.class)
    public ProblemDetail handleEmpresaNotFoundException(EmpresaNotFoundException ex) {
        return ProblemDetail
                .forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(SedeNotFoundException.class)
    public ProblemDetail handleSedeNotFoundException(SedeNotFoundException ex) {
        return ProblemDetail
                .forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(EmpresaParametroNotFoundException.class)
    public ProblemDetail handleEmpresaParametroNotFoundException(EmpresaParametroNotFoundException ex) {
        return ProblemDetail
                .forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(SedeParametroNotFoundException.class)
    public ProblemDetail handleSedeParametroNotFoundException(SedeParametroNotFoundException ex) {
        return ProblemDetail
                .forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(UsuarioNotFoundException.class)
    public ProblemDetail handleUsuarioNotFoundException(UsuarioNotFoundException ex) {
        return ProblemDetail
                .forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }
}