package com.example.invoicepaymentapi.application.usecase.auth;

import com.example.invoicepaymentapi.application.usecase.auth.dto.LoginRequestDto;
import com.example.invoicepaymentapi.application.usecase.auth.dto.LoginResponseDto;
import com.example.invoicepaymentapi.domain.exception.DomainValidationException;
import com.example.invoicepaymentapi.domain.exception.UnauthorizedException;
import com.example.invoicepaymentapi.domain.model.user.*;
import com.example.invoicepaymentapi.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * ログインユースケースのテスト
 */
@ExtendWith(MockitoExtension.class)
class LoginUseCaseTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private LoginUseCase loginUseCase;

    private User validUser;

    @BeforeAll
    static void setUpEnvironment() {
        // テスト用の環境変数を設定
        System.setProperty("JWT_SECRET", "test-secret-key-for-unit-testing-purposes-only-minimum-length-required");
        System.setProperty("JWT_EXPIRATION", "3600");
    }

    @BeforeEach
    void setUp() {
        // テスト用のユーザーを作成（ID付きで再構築）
        CompanyName companyName = CompanyName.create("株式会社サンプル");
        UserName name = UserName.create("山田太郎");
        Email email = Email.create("yamada@example.com");
        Password password = Password.create("Password123!");
        HashedPassword hashedPassword = HashedPassword.create(password);
        UserId userId = UserId.reconstruct(1);
        validUser = User.reconstruct(
                userId,
                companyName,
                name,
                email,
                hashedPassword,
                java.time.LocalDateTime.now(),
                java.time.LocalDateTime.now()
        );
    }

    @Nested
    @DisplayName("正常系")
    class NormalCase {
        @Test
        @DisplayName("正しいメールアドレスとパスワードでログインが成功する")
        void shouldLoginWithValidCredentials() {
            // Given
            LoginRequestDto requestDto = new LoginRequestDto(
                    "yamada@example.com",
                    "Password123!"
            );
            when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(validUser));

            // When
            LoginResponseDto response = loginUseCase.execute(requestDto);

            // Then
            assertThat(response.accessToken()).isNotNull();
            assertThat(response.accessToken()).isNotEmpty();
        }
    }

    @Nested
    @DisplayName("異常系")
    class AbnormalCase {
        @Test
        @DisplayName("必須項目が不足している場合、DomainValidationExceptionをスローする")
        void shouldThrowDomainValidationExceptionWhenRequiredFieldsAreMissing() {
            // Given - emailがnull
            LoginRequestDto requestDto = new LoginRequestDto(null, "Password123!");

            // When & Then
            assertThatThrownBy(() -> loginUseCase.execute(requestDto))
                    .isInstanceOf(DomainValidationException.class);
        }

        @Test
        @DisplayName("メールアドレスの形式が不正な場合、UnauthorizedExceptionをスローする")
        void shouldThrowUnauthorizedExceptionWhenEmailFormatIsInvalid() {
            // Given
            LoginRequestDto requestDto = new LoginRequestDto("invalid-email", "Password123!");

            // When & Then
            assertThatThrownBy(() -> loginUseCase.execute(requestDto))
                    .isInstanceOf(UnauthorizedException.class);
        }

        @Test
        @DisplayName("存在しないメールアドレスの場合、UnauthorizedExceptionをスローする")
        void shouldThrowUnauthorizedExceptionWhenEmailDoesNotExist() {
            // Given
            LoginRequestDto requestDto = new LoginRequestDto(
                    "nonexistent@example.com",
                    "Password123!"
            );
            when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> loginUseCase.execute(requestDto))
                    .isInstanceOf(UnauthorizedException.class);
        }

        @Test
        @DisplayName("パスワードが不正な場合、UnauthorizedExceptionをスローする")
        void shouldThrowUnauthorizedExceptionWhenPasswordIsInvalid() {
            // Given
            LoginRequestDto requestDto = new LoginRequestDto(
                    "yamada@example.com",
                    "WrongPassword123!" // 形式は正しいが、パスワードが間違っている
            );
            when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(validUser));

            // When & Then
            assertThatThrownBy(() -> loginUseCase.execute(requestDto))
                    .isInstanceOf(UnauthorizedException.class);
        }
    }
}

