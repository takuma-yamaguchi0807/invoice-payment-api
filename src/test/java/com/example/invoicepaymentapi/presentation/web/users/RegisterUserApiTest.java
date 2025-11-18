package com.example.invoicepaymentapi.presentation.web.users;

import com.example.invoicepaymentapi.application.usecase.users.RegisterUserUseCase;
import com.example.invoicepaymentapi.domain.exception.ConflictException;
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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * ユーザー登録APIのテスト
 */
@WebMvcTest(UserController.class)
class RegisterUserApiTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    RegisterUserUseCase registerUserUseCase;

    @Nested
    @DisplayName("正常系")
    class NormalCase {
        @Test
        @DisplayName("必須項目のみのリクエストでユーザー登録が成功する")
        void shouldRegisterUserWithRequiredFieldsOnly() throws Exception {
            // Given
            RegisterUserRequest request = new RegisterUserRequest(
                    "株式会社サンプル",
                    "山田太郎",
                    "yamada@example.com",
                    "Password123!"
            );
            doNothing().when(registerUserUseCase).execute(any());

            // When & Then
            mockMvc.perform(post("/api/v1/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated());
        }

        @Test
        @DisplayName("すべての項目を含めたリクエストでユーザー登録が成功する")
        void shouldRegisterUserWithAllFields() throws Exception {
            // Given
            RegisterUserRequest request = new RegisterUserRequest(
                    "株式会社サンプル",
                    "山田太郎",
                    "yamada@example.com",
                    "Password123!"
            );
            doNothing().when(registerUserUseCase).execute(any());

            // When & Then
            mockMvc.perform(post("/api/v1/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated());
        }
    }

    @Nested
    @DisplayName("異常系")
    class AbnormalCase {
        @Test
        @DisplayName("メールアドレスが既に存在する場合、409 Conflictを返す")
        void shouldReturn409WhenEmailAlreadyExists() throws Exception {
            // Given
            RegisterUserRequest request = new RegisterUserRequest(
                    "株式会社サンプル",
                    "山田太郎",
                    "yamada@example.com",
                    "Password123!"
            );
            doThrow(new ConflictException("Email already exists"))
                    .when(registerUserUseCase).execute(any());

            // When & Then
            mockMvc.perform(post("/api/v1/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict());
        }

        @Test
        @DisplayName("必須項目が不足している場合、400 Bad Requestを返す")
        void shouldReturn400WhenRequiredFieldsAreMissing() throws Exception {
            // Given - companyNameが不足
            String requestJson = """
                    {
                        "name": "山田太郎",
                        "email": "yamada@example.com",
                        "password": "Password123!"
                    }
                    """;

            // When & Then
            mockMvc.perform(post("/api/v1/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("メールアドレスの形式が不正な場合、400 Bad Requestを返す")
        void shouldReturn400WhenEmailFormatIsInvalid() throws Exception {
            // Given
            RegisterUserRequest request = new RegisterUserRequest(
                    "株式会社サンプル",
                    "山田太郎",
                    "invalid-email",
                    "Password123!"
            );
            doNothing().when(registerUserUseCase).execute(any());

            // When & Then
            mockMvc.perform(post("/api/v1/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }
}
