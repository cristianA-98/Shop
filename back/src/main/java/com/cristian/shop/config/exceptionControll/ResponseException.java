package com.cristian.shop.config.exceptionControll;


import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ResponseException extends RuntimeException {
    private final String code;
    private final HttpStatus httpStatus;

    public ResponseException(String code, String message, HttpStatus httpStatus) {
        super(message);
        this.code = code;
        this.httpStatus = httpStatus;
    }
}
