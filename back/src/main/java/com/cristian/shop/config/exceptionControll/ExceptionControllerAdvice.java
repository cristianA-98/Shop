package com.cristian.shop.config.exceptionControll;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestControllerAdvice
public class ExceptionControllerAdvice {

    @ExceptionHandler(ResponseException.class)
    public ResponseEntity<Map<String, String>> ResponseException(ResponseException e) {
        Map<String, String> messageError = new HashMap<>();
        messageError.put(e.getCode(), e.getMessage());
        return new ResponseEntity<>(messageError, e.getHttpStatus());
    }


    ///---                Validator                 --- ///

    // exeptions for validations on forms login and register
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> ValidateError(MethodArgumentNotValidException e) {
        Map<String, String> response = extractMessageErrorsDTO(e.getBindingResult());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    //Extraction of error message, from the validation of the forms.
    private Map<String, String> extractMessageErrorsDTO(BindingResult result) {
        Map<String, String> response = new HashMap<>();
        for (FieldError item : result.getFieldErrors()) {
            response.put(item.getField().toUpperCase(), Objects.requireNonNull(item.getDefaultMessage()).toUpperCase());
        }
        return response;
    }
}
