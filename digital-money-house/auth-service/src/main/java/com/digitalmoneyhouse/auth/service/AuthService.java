package com.digitalmoneyhouse.auth.service;

import com.digitalmoneyhouse.auth.client.UsersServiceClient;
import com.digitalmoneyhouse.auth.client.dto.ValidateCredentialsRequestDto;
import com.digitalmoneyhouse.auth.client.dto.ValidateCredentialsResponseDto;
import com.digitalmoneyhouse.auth.dto.LoginRequest;
import com.digitalmoneyhouse.auth.dto.LoginResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UsersServiceClient usersServiceClient;
    private final JwtService jwtService;

    public AuthService(UsersServiceClient usersServiceClient, JwtService jwtService) {
        this.usersServiceClient = usersServiceClient;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest request) {
        ValidateCredentialsRequestDto dto = new ValidateCredentialsRequestDto(
            request.email().trim().toLowerCase(),
            request.password()
        );
        ResponseEntity<ValidateCredentialsResponseDto> response = usersServiceClient.validateCredentials(dto);
        if (response.getStatusCode().isError() || response.getBody() == null) {
            throw new InvalidCredentialsException("Usuario o contraseña incorrectos");
        }
        ValidateCredentialsResponseDto user = response.getBody();
        String token = jwtService.generateToken(user.userId(), user.email(), user.roleName());
        return LoginResponse.of(token, user.userId(), user.email());
    }
}
