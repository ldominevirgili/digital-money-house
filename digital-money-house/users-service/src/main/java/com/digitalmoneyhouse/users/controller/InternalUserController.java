package com.digitalmoneyhouse.users.controller;

import com.digitalmoneyhouse.users.dto.ValidateCredentialsRequest;
import com.digitalmoneyhouse.users.dto.ValidateCredentialsResponse;
import com.digitalmoneyhouse.users.entity.User;
import com.digitalmoneyhouse.users.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/internal")
public class InternalUserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public InternalUserController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/validate-credentials")
    public ResponseEntity<ValidateCredentialsResponse> validateCredentials(
            @Valid @RequestBody ValidateCredentialsRequest request) {
        User user = userRepository.findByEmail(request.email().trim().toLowerCase())
            .orElse(null);
        if (user == null || !passwordEncoder.matches(request.password(), user.getPassword())) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(new ValidateCredentialsResponse(
            user.getId(),
            user.getEmail(),
            user.getRole().getName()
        ));
    }
}
