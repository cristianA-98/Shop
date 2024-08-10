package com.cristian.shop.controller.user;

import com.cristian.shop.dto.UserDTO;
import com.cristian.shop.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth/user/")
@AllArgsConstructor
public class UserController {

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
        Map<String, String> body = userService.registerUser(user);
        return new ResponseEntity<>(body, HttpStatus.ACCEPTED);
    }

}
