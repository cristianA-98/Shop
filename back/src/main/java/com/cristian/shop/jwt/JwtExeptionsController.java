package com.cristian.shop.jwt;


import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class JwtExeptionsController {
    
    @ExceptionHandler(JwtException.class)
    public ResponseEntity<Map<String, String>> handleExpiredJwtException(JwtException e, WebRequest request) {
        Map<String, String> messageError = new HashMap<>();

        if (e.getLocalizedMessage().contains(":")) {
            String error = e.getMessage().substring(0, e.getMessage().indexOf(":"));
            messageError.put("JWT", error);
            log.error(error);
            return new ResponseEntity<>(messageError, HttpStatus.NOT_FOUND);
        }

        if (e.getLocalizedMessage().contains(".")) {
            String error = e.getMessage().substring(0, e.getMessage().indexOf("."));
            messageError.put("JWT", error);

            log.error(error);
            return new ResponseEntity<>(messageError, HttpStatus.NOT_FOUND);
        }

        messageError.put("SERVER", "INTERNAL SERVER ERROR");
        return new ResponseEntity<>(messageError, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
