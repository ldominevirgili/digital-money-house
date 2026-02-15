package com.digitalmoneyhouse.users;

import com.digitalmoneyhouse.users.client.AccountServiceClient;
import com.digitalmoneyhouse.users.client.dto.AccountResponseDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountServiceClient accountServiceClient;

    @Test
    void register_withValidData_returns201AndUserWithCvuAndAlias() throws Exception {
        when(accountServiceClient.createAccount(any()))
            .thenReturn(new AccountResponseDto(1L, 1L, "1234567890123456789012", "dinero.casa.digital", BigDecimal.ZERO));

        String body = """
            {
              "firstName": "Juan",
              "lastName": "Pérez",
              "email": "juan.perez@test.com",
              "password": "password123"
            }
            """;

        mockMvc.perform(post("/users/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.firstName").value("Juan"))
            .andExpect(jsonPath("$.lastName").value("Pérez"))
            .andExpect(jsonPath("$.email").value("juan.perez@test.com"))
            .andExpect(jsonPath("$.cvu").value("1234567890123456789012"))
            .andExpect(jsonPath("$.alias").value("dinero.casa.digital"));
    }

    @Test
    void register_withInvalidEmail_returns400() throws Exception {
        String body = """
            {
              "firstName": "Juan",
              "lastName": "Pérez",
              "email": "not-an-email",
              "password": "password123"
            }
            """;

        mockMvc.perform(post("/users/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isBadRequest());
    }
}
