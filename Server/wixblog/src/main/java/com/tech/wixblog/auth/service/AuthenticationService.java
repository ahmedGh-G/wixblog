package com.tech.wixblog.auth.service;

import com.tech.wixblog.auth.dto.LoginRequest;
import com.tech.wixblog.auth.dto.LoginResponse;
import com.tech.wixblog.auth.dto.RegisterRequest;
import com.tech.wixblog.auth.dto.RegisterResponse;
import com.tech.wixblog.common.exception.ResourceAlreadyExistsException;
import com.tech.wixblog.security.JwtService;
import com.tech.wixblog.user.domain.Role;
import com.tech.wixblog.user.domain.User;
import com.tech.wixblog.user.domain.UserStatus;
import com.tech.wixblog.user.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@Transactional
public class AuthenticationService {

    private final UserRepository userRepository;

    private final JwtService jwtService;

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthenticationService(
            UserRepository userRepository, JwtService jwtService,
            PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager
                                ) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    public RegisterResponse register(
        RegisterRequest request
    ) {

        String email = normalizeEmail(request.email());
        String username = normalizeUsername(request.username());

        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new ResourceAlreadyExistsException(
                "An account with this email already exists."
            );
        }

        if (userRepository.existsByUsernameIgnoreCase(username)) {
            throw new ResourceAlreadyExistsException(
                "This username is already taken."
            );
        }

        String passwordHash =
            passwordEncoder.encode(request.password());

        User user = new User(
            email,
            username,
            passwordHash,
            Role.USER,
            UserStatus.ACTIVE
        );

        User savedUser = userRepository.save(user);

        return RegisterResponse.from(savedUser);
    }


    public LoginResponse login (
            LoginRequest request
                               ) {

        String email =
                request.email()
                        .trim()
                        .toLowerCase(Locale.ROOT);

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                email,
                                request.password()
                        )
                                                  );

        User user =
                userRepository
                        .findByEmailIgnoreCase(email)
                        .orElseThrow(() ->
                                             new UsernameNotFoundException(
                                                     "User not found"
                                             )
                                    );

        String accessToken =
                jwtService.generateAccessToken(user);

        return new LoginResponse(
                accessToken,
                "Bearer",
                jwtService.getAccessTokenExpirationSeconds(),
                user.getId(),
                user.getUsername(),
                user.getRole()
        );
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private String normalizeUsername(String username) {
        return username.trim();
    }
}