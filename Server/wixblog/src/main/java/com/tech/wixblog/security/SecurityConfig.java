package com.tech.wixblog.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder () {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain (
            HttpSecurity http
                                                   ) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                                           session.sessionCreationPolicy(
                                                   SessionCreationPolicy.STATELESS
                                                                        )
                                  )

                .authorizeHttpRequests(auth -> auth
                                               .requestMatchers(
                                                       "/api/v1/auth/register",
                                                       "/api/v1/auth/login",
                                                       "/v3/api-docs/**",
                                                       "/swagger-ui/**",
                                                       "/swagger-ui.html"
                                                               ).permitAll()
                                               .requestMatchers(HttpMethod.GET, "/api/v1/stories/me").authenticated()
                                               .requestMatchers(HttpMethod.GET,
                                                                "/api/v1/users/**",
                                                                "/api/v1/stories/*").permitAll()

                                               .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                                               .anyRequest().authenticated()

                                      )
                .oauth2ResourceServer(oauth2 ->
                                              oauth2.jwt(jwt -> {
                                              })
                                     );

        return http.build();
    }
}