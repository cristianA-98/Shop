package com.cristian.shop.service;


import com.cristian.shop.Model.User;
import com.cristian.shop.config.exceptionControll.ResponseException;
import com.cristian.shop.dto.EmailResetPasswordDTO;
import com.cristian.shop.dto.ResetPasswordDTO;
import com.cristian.shop.dto.UserDTO;
import com.cristian.shop.enum_.Rol;
import com.cristian.shop.jwt.JwtService;
import com.cristian.shop.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final EmailService emailService;


    //Authenticacion

    public Map<String, String> authenticacion(UserDTO user) {
        authenticationAccount(user);
        return Map.of("Jwt", jwtService.generateJwt(user.getEmail()));
    }

    public Map<String, String> register(UserDTO userDTO) {
        userRepository.save(toUser(userDTO));
        return Map.of("JWT", jwtService.generateJwt(userDTO.getEmail()));
    }


    public void sendPasswordResetLink(EmailResetPasswordDTO passwordDTO) {
        User user = userRepository.findByEmail(passwordDTO.getEmail())
                .map(user1 -> {
                    isValidRol(user1.getRol());
                    return user1;
                })
                .orElseThrow(() -> new ResponseException("404", "Email in used", HttpStatus.NOT_ACCEPTABLE));
        emailService.sendEmail(
                user.getEmail(),
                "prueba para reset password",
                jwtService.generateJwtPasswordReset(user.getEmail())
        );

    }

    public void applyNewPassword(ResetPasswordDTO resetPasswordDTO, String token) {
        jwtService.validateJwt(token);
        User user = userRepository.findByEmail(jwtService.extractJwt(token)).orElseThrow(
                () -> new ResponseException("505", "INTERNAL SERVER ERROR", HttpStatus.INTERNAL_SERVER_ERROR)
        );

        if (!resetPasswordDTO.isPasswordsMatch())
            throw new ResponseException("400", "Passwords do not match", HttpStatus.BAD_REQUEST);
        userRepository.changePassword(encoder.encode(resetPasswordDTO.getPassword()), user.getId());
    }

    private User toUser(UserDTO userDto) {
        emailInUsed(userDto);
        isValidRol(userDto.getRol());
        userDto.setPassword(encoder.encode(userDto.getPassword()));
        userDto.setRol(userDto.getRol().toUpperCase());
        return mapper.map(userDto, User.class);
    }

    private void emailInUsed(UserDTO userDto) {
        if (userRepository.findByEmail(userDto.getEmail()).isPresent())
            throw new ResponseException("404", "Email in used", HttpStatus.NOT_ACCEPTABLE);
    }


    private void authenticationAccount(UserDTO userDto) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    userDto.getEmail(),
                    userDto.getPassword()
            ));
        } catch (Exception e) {
            if (e.getLocalizedMessage().equals("Bad credentials"))
                throw new ResponseException("404", "Incorrect password", HttpStatus.NOT_FOUND);
            throw new ResponseException("404", "Incorrect password", HttpStatus.NOT_FOUND);
        }
    }

    private void isValidRol(String rol) {
        Rol.fromString(rol);
    }

    private User getUserContext() {
        final String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email).orElseThrow(
                () -> new ResponseException("505", "INTERNAL SERVER ERROR", HttpStatus.INTERNAL_SERVER_ERROR)
        );
    }


}
