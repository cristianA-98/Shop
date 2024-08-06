package com.cristian.shop.config;


import com.cristian.shop.jwt.JwtFilterAuthentication;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration()
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {

    private final JwtFilterAuthentication filterAuthentication;
    private final AuthenticationProvider authenticationProvider;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.
                csrf(x -> x.disable()).
                authorizeHttpRequests(x ->
                        x.antMatchers("/api/v1/auth/**").permitAll()
                                .antMatchers("/api/v1/user/**").hasAnyAuthority("USER")
                                .antMatchers("/api/v1/admin/**").hasAnyAuthority("ADMIN")
                                .anyRequest().authenticated()
                ).
                sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(filterAuthentication, UsernamePasswordAuthenticationFilter.class);


        return http.build();
    }
}
