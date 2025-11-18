package com.example.invoicepaymentapi.presentation.web.auth;

import com.example.invoicepaymentapi.application.usecase.auth.LoginUseCase;
import com.example.invoicepaymentapi.application.usecase.auth.dto.LoginResponseDto;
import com.example.invoicepaymentapi.domain.exception.UnauthorizedException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * ログインAPIのテスト
 */
@WebMvcTest(LoginController.class)
class LoginApiTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    LoginUseCase loginUseCase;

    @Nested
    @DisplayName("正常系")
    class NormalCase {
        @Test
        @DisplayName("必須項目のみのリクエストでログインが成功する")
        void shouldLoginWithRequiredFieldsOnly() throws Exception {
            // Given
            LoginRequest request = new LoginRequest(
                    "yamada@example.com",
                    "Password123!"
            );
            LoginResponseDto responseDto = new LoginResponseDto("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.example");
            when(loginUseCase.execute(any())).thenReturn(responseDto);

            // When & Then
            mockMvc.perform(post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.accessToken").exists());
        }

        @Test
        @DisplayName("すべての項目を含めたリクエストでログインが成功する")
        void shouldLoginWithAllFields() throws Exception {
            // Given
            LoginRequest request = new LoginRequest(
                    "yamada@example.com",
                    "Password123!"
            );
            LoginResponseDto responseDto = new LoginResponseDto("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.example");
            when(loginUseCase.execute(any())).thenReturn(responseDto);

            // When & Then
            mockMvc.perform(post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.accessToken").exists());
        }
    }

    @Nested
    @DisplayName("異常系")
    class AbnormalCase {
        @Test
        @DisplayName("認証に失敗した場合、401 Unauthorizedを返す")
        void shouldReturn401WhenAuthenticationFails() throws Exception {
            // Given
            LoginRequest request = new LoginRequest(
                    "yamada@example.com",
                    "WrongPassword"
            );
            when(loginUseCase.execute(any()))
                    .thenThrow(new UnauthorizedException("Authentication failed"));

            // When & Then
            mockMvc.perform(post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value("UNAUTHORIZED"))
                    .andExpect(jsonPath("$.message").exists());
        }

        @Test
        @DisplayName("必須項目が不足している場合、400 Bad Requestを返す")
        void shouldReturn400WhenRequiredFieldsAreMissing() throws Exception {
            // Given - emailが不足
            String requestJson = """
                    {
                        "password": "Password123!"
                    }
                    """;

            // When & Then
            mockMvc.perform(post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("メールアドレスの形式が不正な場合、400 Bad Requestを返す")
        void shouldReturn400WhenEmailFormatIsInvalid() throws Exception {
            // Given
            LoginRequest request = new LoginRequest(
                    "invalid-email",
                    "Password123!"
            );
            when(loginUseCase.execute(any()))
                    .thenThrow(new UnauthorizedException("Authentication failed"));

            // When & Then
            mockMvc.perform(post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }
}
