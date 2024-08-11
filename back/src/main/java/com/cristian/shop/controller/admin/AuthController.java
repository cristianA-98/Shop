package com.cristian.shop.controller.admin;

import com.cristian.shop.dto.EmailResetPasswordDTO;
import com.cristian.shop.dto.ResetPasswordDTO;
import com.cristian.shop.dto.UserDTO;
import com.cristian.shop.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth/admin/")
@AllArgsConstructor
public class AuthController {

    private final UserService userService;

    // Authentication.
    @PostMapping("authentiaction")
    public ResponseEntity<Map<String, String>> authentiaction(@Valid() @RequestBody UserDTO user) {
        Map<String, String> body = userService.authenticacion(user);
        return new ResponseEntity<>(body, HttpStatus.ACCEPTED);
    }

    // Register
    @PostMapping("register")
    public ResponseEntity<Map<String, String>> register(@Valid() @RequestBody UserDTO user) {
        Map<String, String> body = userService.register(user);
        return new ResponseEntity<>(body, HttpStatus.ACCEPTED);
    }

    @PostMapping("sendResetPassword")
    public ResponseEntity<Map<String, String>> sendResetPassword(@Valid() @RequestBody EmailResetPasswordDTO emailResetPasswordDTO) {
        userService.sendPasswordResetLink(emailResetPasswordDTO);
        return new ResponseEntity<>(Map.of("msg", "The email was sent to change the password"), HttpStatus.ACCEPTED);
    }

    @PostMapping("resetPassword")
    public ResponseEntity<Map<String, String>> resetPassword(@Valid() @RequestParam("jwt") String token, @RequestBody ResetPasswordDTO resetPasswordDTO) {
        userService.applyNewPassword(resetPasswordDTO, token);
        return new ResponseEntity<>(Map.of("msg", "The password was changed successfully"), HttpStatus.ACCEPTED);
    }
}
