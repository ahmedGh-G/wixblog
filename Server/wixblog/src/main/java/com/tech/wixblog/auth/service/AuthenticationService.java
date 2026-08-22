package com.tech.wixblog.auth.service;

import com.tech.wixblog.auth.dto.RegisterRequest;
import com.tech.wixblog.auth.dto.RegisterResponse;
import com.tech.wixblog.common.exception.ResourceAlreadyExistsException;
import com.tech.wixblog.user.domain.Role;
import com.tech.wixblog.user.domain.User;
import com.tech.wixblog.user.domain.UserStatus;
import com.tech.wixblog.user.repository.UserRepository;
import org.hibernate.annotations.Audited;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@Transactional
public class AuthenticationService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public AuthenticationService(
        UserRepository userRepository,
        PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private String normalizeUsername(String username) {
        return username.trim();
    }
}