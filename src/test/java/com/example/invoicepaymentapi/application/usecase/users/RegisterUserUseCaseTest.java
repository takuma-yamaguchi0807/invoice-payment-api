package com.example.invoicepaymentapi.application.usecase.users;

import com.example.invoicepaymentapi.application.usecase.users.dto.RegisterUserRequestDto;
import com.example.invoicepaymentapi.domain.exception.ConflictException;
import com.example.invoicepaymentapi.domain.exception.DomainValidationException;
import com.example.invoicepaymentapi.domain.model.user.Email;
import com.example.invoicepaymentapi.domain.model.user.User;
import com.example.invoicepaymentapi.domain.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * ユーザー登録ユースケースのテスト
 */
@ExtendWith(MockitoExtension.class)
class RegisterUserUseCaseTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RegisterUserUseCase registerUserUseCase;

    @Nested
    @DisplayName("正常系")
    class NormalCase {
        @Test
        @DisplayName("必須項目のみのリクエストでユーザー登録が成功する")
        void shouldRegisterUserWithRequiredFieldsOnly() {
            // Given
            RegisterUserRequestDto requestDto = new RegisterUserRequestDto(
                    "株式会社サンプル",
                    "山田太郎",
                    "yamada@example.com",
                    "Password123!"
            );
            when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.empty());
            doNothing().when(userRepository).save(any(User.class));

            // When
            registerUserUseCase.execute(requestDto);

            // Then
            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            verify(userRepository, times(1)).save(userCaptor.capture());
            User savedUser = userCaptor.getValue();
            assertThat(savedUser.email().value()).isEqualTo("yamada@example.com");
            assertThat(savedUser.name().value()).isEqualTo("山田太郎");
        }

        @Test
        @DisplayName("すべての項目を含めたリクエストでユーザー登録が成功する")
        void shouldRegisterUserWithAllFields() {
            // Given
            RegisterUserRequestDto requestDto = new RegisterUserRequestDto(
                    "株式会社サンプル",
                    "山田太郎",
                    "yamada@example.com",
                    "Password123!"
            );
            when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.empty());
            doNothing().when(userRepository).save(any(User.class));

            // When
            registerUserUseCase.execute(requestDto);

            // Then
            verify(userRepository, times(1)).save(any(User.class));
        }
    }

    @Nested
    @DisplayName("異常系")
    class AbnormalCase {
        @Test
        @DisplayName("必須項目が不足している場合、DomainValidationExceptionをスローする")
        void shouldThrowDomainValidationExceptionWhenRequiredFieldsAreMissing() {
            // Given - companyNameがnull
            RegisterUserRequestDto requestDto = new RegisterUserRequestDto(
                    null,
                    "山田太郎",
                    "yamada@example.com",
                    "Password123!"
            );

            // When & Then
            assertThatThrownBy(() -> registerUserUseCase.execute(requestDto))
                    .isInstanceOf(DomainValidationException.class);
        }

        @Test
        @DisplayName("メールアドレスが既に存在する場合、ConflictExceptionをスローする")
        void shouldThrowConflictExceptionWhenEmailAlreadyExists() {
            // Given
            RegisterUserRequestDto requestDto = new RegisterUserRequestDto(
                    "株式会社サンプル",
                    "山田太郎",
                    "yamada@example.com",
                    "Password123!"
            );
            when(userRepository.findByEmail(any(Email.class)))
                    .thenReturn(Optional.of(mock(User.class)));

            // When & Then
            assertThatThrownBy(() -> registerUserUseCase.execute(requestDto))
                    .isInstanceOf(ConflictException.class);
        }

        @Test
        @DisplayName("メールアドレスの形式が不正な場合、DomainValidationExceptionをスローする")
        void shouldThrowDomainValidationExceptionWhenEmailFormatIsInvalid() {
            // Given
            RegisterUserRequestDto requestDto = new RegisterUserRequestDto(
                    "株式会社サンプル",
                    "山田太郎",
                    "invalid-email",
                    "Password123!"
            );

            // When & Then
            assertThatThrownBy(() -> registerUserUseCase.execute(requestDto))
                    .isInstanceOf(DomainValidationException.class);
        }

        @Test
        @DisplayName("パスワードが8文字未満の場合、DomainValidationExceptionをスローする")
        void shouldThrowDomainValidationExceptionWhenPasswordIsLessThan8Characters() {
            // Given
            RegisterUserRequestDto requestDto = new RegisterUserRequestDto(
                    "株式会社サンプル",
                    "山田太郎",
                    "yamada@example.com",
                    "Pass12!"
            );

            // When & Then
            assertThatThrownBy(() -> registerUserUseCase.execute(requestDto))
                    .isInstanceOf(DomainValidationException.class);
        }

        @Test
        @DisplayName("パスワードの文字種が不足している場合、DomainValidationExceptionをスローする")
        void shouldThrowDomainValidationExceptionWhenPasswordHasInsufficientCharacterTypes() {
            // Given - 小文字のみ（1種類）
            RegisterUserRequestDto requestDto = new RegisterUserRequestDto(
                    "株式会社サンプル",
                    "山田太郎",
                    "yamada@example.com",
                    "password"
            );

            // When & Then
            assertThatThrownBy(() -> registerUserUseCase.execute(requestDto))
                    .isInstanceOf(DomainValidationException.class);
        }

        @Test
        @DisplayName("companyNameが255文字を超える場合、DomainValidationExceptionをスローする")
        void shouldThrowDomainValidationExceptionWhenCompanyNameExceedsMaxLength() {
            // Given
            String longCompanyName = "a".repeat(256);
            RegisterUserRequestDto requestDto = new RegisterUserRequestDto(
                    longCompanyName,
                    "山田太郎",
                    "yamada@example.com",
                    "Password123!"
            );

            // When & Then
            assertThatThrownBy(() -> registerUserUseCase.execute(requestDto))
                    .isInstanceOf(DomainValidationException.class);
        }

        @Test
        @DisplayName("nameが255文字を超える場合、DomainValidationExceptionをスローする")
        void shouldThrowDomainValidationExceptionWhenNameExceedsMaxLength() {
            // Given
            String longName = "a".repeat(256);
            RegisterUserRequestDto requestDto = new RegisterUserRequestDto(
                    "株式会社サンプル",
                    longName,
                    "yamada@example.com",
                    "Password123!"
            );

            // When & Then
            assertThatThrownBy(() -> registerUserUseCase.execute(requestDto))
                    .isInstanceOf(DomainValidationException.class);
        }

        @Test
        @DisplayName("emailが254文字を超える場合、DomainValidationExceptionをスローする")
        void shouldThrowDomainValidationExceptionWhenEmailExceedsMaxLength() {
            // Given
            String longEmail = "a".repeat(245) + "@example.com"; // 254文字を超える
            RegisterUserRequestDto requestDto = new RegisterUserRequestDto(
                    "株式会社サンプル",
                    "山田太郎",
                    longEmail,
                    "Password123!"
            );

            // When & Then
            assertThatThrownBy(() -> registerUserUseCase.execute(requestDto))
                    .isInstanceOf(DomainValidationException.class);
        }
    }
}

