package com.cristian.shop.service;


import com.cristian.shop.config.exceptionControll.ResponseException;
import com.cristian.shop.dto.UserDTO;
import com.cristian.shop.jwt.JwtService;
import com.cristian.shop.mapper.UserMapper;
import com.cristian.shop.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@AllArgsConstructor
public class UserService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder encoder;
    private final UserMapper userMapper;

    //Authenticacion

    public Map<String, String> authenticacion(UserDTO user) {

        Authentication authentication = authenticationManager
                .authenticate(
                        new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword())
                );

        return Map.of("Jwt", jwtService.generateToken(authentication.getName()));
    }

    //Register

    public Map<String, String> register(UserDTO user) {

        if (userRepository.findByEmail(user.getEmail()).isPresent())
            throw new ResponseException("Email", "Email in used", HttpStatus.CONFLICT);


        user.setPassword(encoder.encode(user.getPassword()));
        user.setRol("USER");

        userRepository.save(userMapper.toUser(user));

        return Map.of("JWT", jwtService.generateToken(user.getEmail()));
    }

    public Map<String, String> registerAdmin(UserDTO user) {

        if (userRepository.findByEmail(user.getEmail()).isPresent())
            throw new ResponseException("Email", "Email in used", HttpStatus.CONFLICT);

        user.setPassword(encoder.encode(user.getPassword()));
        user.setRol("ADMIN");

        userRepository.save(userMapper.toUser(user));

        return Map.of("JWT", jwtService.generateToken(user.getEmail()));
    }


}
