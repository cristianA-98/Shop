package com.cristian.shop.enum_;

import com.cristian.shop.config.exceptionControll.ResponseException;
import org.springframework.http.HttpStatus;

public enum Rol {
    ADMIN, USER;

    // Static method to check if the value is valid
    public static void fromString(String role) {
        try {
            Rol.valueOf(role.toUpperCase());
        } catch (Exception e) {
            throw new ResponseException("404", "Rol not found", HttpStatus.NOT_ACCEPTABLE);
        }
    }
}
