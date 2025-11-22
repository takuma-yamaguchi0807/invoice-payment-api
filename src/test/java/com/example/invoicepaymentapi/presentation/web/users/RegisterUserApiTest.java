package com.example.invoicepaymentapi.presentation.web.users;

import com.example.invoicepaymentapi.application.usecase.users.RegisterUserUseCase;
import com.example.invoicepaymentapi.domain.exception.ConflictException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
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
 * 
 * TODO: 後でユニットテストで事前ログインするような汎用クラスを作成する
 *       認証が必要なエンドポイントのテストで使用する
 */
@WebMvcTest(
        controllers = UserController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class
)
@AutoConfigureMockMvc(addFilters = false)
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

        @Test
        @DisplayName("パスワードが8文字未満の場合、400 Bad Requestを返す")
        void shouldReturn400WhenPasswordIsLessThan8Characters() throws Exception {
            // Given
            RegisterUserRequest request = new RegisterUserRequest(
                    "株式会社サンプル",
                    "山田太郎",
                    "yamada@example.com",
                    "Pass12!"
            );
            doNothing().when(registerUserUseCase).execute(any());

            // When & Then
            mockMvc.perform(post("/api/v1/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("パスワードの文字種が不足している場合、400 Bad Requestを返す")
        void shouldReturn400WhenPasswordHasInsufficientCharacterTypes() throws Exception {
            // Given - 小文字のみ（1種類）
            RegisterUserRequest request = new RegisterUserRequest(
                    "株式会社サンプル",
                    "山田太郎",
                    "yamada@example.com",
                    "password"
            );
            doNothing().when(registerUserUseCase).execute(any());

            // When & Then
            mockMvc.perform(post("/api/v1/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("companyNameが255文字を超える場合、400 Bad Requestを返す")
        void shouldReturn400WhenCompanyNameExceedsMaxLength() throws Exception {
            // Given
            String longCompanyName = "a".repeat(256);
            RegisterUserRequest request = new RegisterUserRequest(
                    longCompanyName,
                    "山田太郎",
                    "yamada@example.com",
                    "Password123!"
            );
            doNothing().when(registerUserUseCase).execute(any());

            // When & Then
            mockMvc.perform(post("/api/v1/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("nameが255文字を超える場合、400 Bad Requestを返す")
        void shouldReturn400WhenNameExceedsMaxLength() throws Exception {
            // Given
            String longName = "a".repeat(256);
            RegisterUserRequest request = new RegisterUserRequest(
                    "株式会社サンプル",
                    longName,
                    "yamada@example.com",
                    "Password123!"
            );
            doNothing().when(registerUserUseCase).execute(any());

            // When & Then
            mockMvc.perform(post("/api/v1/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("emailが254文字を超える場合、400 Bad Requestを返す")
        void shouldReturn400WhenEmailExceedsMaxLength() throws Exception {
            // Given
            String longEmail = "a".repeat(245) + "@example.com"; // 254文字を超える
            RegisterUserRequest request = new RegisterUserRequest(
                    "株式会社サンプル",
                    "山田太郎",
                    longEmail,
                    "Password123!"
            );
            doNothing().when(registerUserUseCase).execute(any());

            // When & Then
            mockMvc.perform(post("/api/v1/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("複数フィールドで複数エラーが発生する場合、400 Bad Requestを返す")
        void shouldReturn400WhenMultipleFieldsHaveErrors() throws Exception {
            // Given - companyName不足、email形式不正、パスワード8文字未満
            String requestJson = """
                    {
                        "name": "山田太郎",
                        "email": "invalid-email",
                        "password": "Pass12!"
                    }
                    """;

            // When & Then
            mockMvc.perform(post("/api/v1/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson))
                    .andExpect(status().isBadRequest());
        }
    }
}
