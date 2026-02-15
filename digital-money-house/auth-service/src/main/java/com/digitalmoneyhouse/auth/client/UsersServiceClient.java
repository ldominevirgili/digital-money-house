package com.digitalmoneyhouse.auth.client;

import com.digitalmoneyhouse.auth.client.dto.ValidateCredentialsRequestDto;
import com.digitalmoneyhouse.auth.client.dto.ValidateCredentialsResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "users-service")
public interface UsersServiceClient {

    @PostMapping("/internal/validate-credentials")
    ResponseEntity<ValidateCredentialsResponseDto> validateCredentials(@RequestBody ValidateCredentialsRequestDto request);
}
