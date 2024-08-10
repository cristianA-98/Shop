package com.cristian.shop.service;


import com.cristian.shop.Model.User;
import com.cristian.shop.config.exceptionControll.ResponseException;
import com.cristian.shop.dto.UserDTO;
import com.cristian.shop.jwt.JwtService;
import com.cristian.shop.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
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
    private final ModelMapper mapper;

    //Authenticacion

    public Map<String, String> authenticacion(UserDTO user) {

        Authentication authentication = authenticationManager
                .authenticate(
                        new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword())
                );

        return Map.of("Jwt", jwtService.generateToken(authentication.getName()));
    }

    //Register

    public Map<String, String> registerUser(UserDTO userDTO) {

        if (userRepository.findByEmail(userDTO.getEmail()).isPresent())
            throw new ResponseException("Email", "Email in used", HttpStatus.CONFLICT);

        userDTO.setPassword(encoder.encode(userDTO.getPassword()));
        userDTO.setRol("USER");

        userRepository.save(mapper.map(userDTO, User.class));

        return Map.of("JWT", jwtService.generateToken(userDTO.getEmail()));
    }

    public Map<String, String> register(UserDTO userDTO) {

        if (userRepository.findByEmail(userDTO.getEmail()).isPresent())
            throw new ResponseException("Email", "Email in used", HttpStatus.CONFLICT);

        userDTO.setPassword(encoder.encode(userDTO.getPassword()));
        userDTO.setRol("ADMIN");

        userRepository.save(mapper.map(userDTO, User.class));

        return Map.of("JWT", jwtService.generateToken(userDTO.getEmail()));
    }


}
