package com.digitalmoneyhouse.users.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
    @NotBlank(message = "nombre requerido")
    @Size(max = 100)
    String firstName,

    @NotBlank(message = "apellido requerido")
    @Size(max = 100)
    String lastName,

    @NotBlank(message = "email requerido")
    @Email(message = "email invalido")
    @Size(max = 255)
    String email,

    @NotBlank(message = "password requerido")
    @Size(min = 6, max = 100)
    String password
) {}
