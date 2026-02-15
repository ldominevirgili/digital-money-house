package com.digitalmoneyhouse.users.service;

import com.digitalmoneyhouse.users.dto.RegisterRequest;
import com.digitalmoneyhouse.users.dto.RegisterResponse;
import com.digitalmoneyhouse.users.dto.UserProfileResponse;
import com.digitalmoneyhouse.users.entity.Role;
import com.digitalmoneyhouse.users.entity.User;
import com.digitalmoneyhouse.users.exception.ValidationException;
import com.digitalmoneyhouse.users.client.AccountServiceClient;
import com.digitalmoneyhouse.users.client.dto.AccountResponseDto;
import com.digitalmoneyhouse.users.client.dto.AccountSummaryDto;
import com.digitalmoneyhouse.users.client.dto.CreateAccountRequestDto;
import com.digitalmoneyhouse.users.exception.ForbiddenException;
import com.digitalmoneyhouse.users.exception.ResourceNotFoundException;
import com.digitalmoneyhouse.users.repository.RoleRepository;
import com.digitalmoneyhouse.users.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private static final String DEFAULT_ROLE = "USER";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AccountServiceClient accountServiceClient;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       AccountServiceClient accountServiceClient,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.accountServiceClient = accountServiceClient;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new ValidationException("Email ya registrado");
        }
        Role role = roleRepository.findByName(DEFAULT_ROLE)
            .orElseGet(() -> roleRepository.save(new Role(DEFAULT_ROLE)));

        User user = new User();
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setEmail(request.email().trim().toLowerCase());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(role);
        user = userRepository.save(user);

        CreateAccountRequestDto accountRequest = new CreateAccountRequestDto(user.getId());
        AccountResponseDto account = accountServiceClient.createAccount(accountRequest);

        return new RegisterResponse(
            user.getId(),
            user.getFirstName(),
            user.getLastName(),
            user.getEmail(),
            account.cvu(),
            account.alias()
        );
    }

    public UserProfileResponse getProfile(Long id, Long authUserId) {
        if (!authUserId.equals(id)) {
            throw new ForbiddenException("Sin permisos");
        }
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        AccountSummaryDto account = accountServiceClient.getAccountByUserId(user.getId());
        return new UserProfileResponse(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(),
                account.cvu(), account.alias());
    }
}
