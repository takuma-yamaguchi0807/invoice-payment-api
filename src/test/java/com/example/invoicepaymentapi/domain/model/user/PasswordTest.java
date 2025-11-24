package com.example.invoicepaymentapi.domain.model.user;

import com.example.invoicepaymentapi.domain.exception.DomainValidationException;
import com.example.invoicepaymentapi.domain.exception.ValidationError;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * パスワード値オブジェクトの単体テスト
 */
class PasswordTest {

    @Nested
    @DisplayName("正常系")
    class NormalCase {
        @Test
        @DisplayName("有効なパスワード（大文字・小文字・数字・記号）で値オブジェクトを作成できる")
        void shouldCreatePasswordWithAllCharacterTypes() {
            // Given
            String password = "Password123!";

            // When
            Password passwordVo = Password.create(password);

            // Then
            assertNotNull(passwordVo);
            assertEquals(password, passwordVo.value());
        }

        @Test
        @DisplayName("有効なパスワード（大文字・小文字・数字）で値オブジェクトを作成できる")
        void shouldCreatePasswordWithThreeCharacterTypes() {
            // Given
            String password = "Password123";

            // When
            Password passwordVo = Password.create(password);

            // Then
            assertNotNull(passwordVo);
            assertEquals(password, passwordVo.value());
        }

        @Test
        @DisplayName("有効なパスワード（大文字・小文字・記号）で値オブジェクトを作成できる")
        void shouldCreatePasswordWithUpperLowerSpecial() {
            // Given
            String password = "Password!@#";

            // When
            Password passwordVo = Password.create(password);

            // Then
            assertNotNull(passwordVo);
            assertEquals(password, passwordVo.value());
        }

        @Test
        @DisplayName("最小長（8文字）のパスワードで値オブジェクトを作成できる")
        void shouldCreatePasswordWithMinimumLength() {
            // Given
            String password = "Pass123!";

            // When
            Password passwordVo = Password.create(password);

            // Then
            assertNotNull(passwordVo);
            assertEquals(password, passwordVo.value());
        }

        @Test
        @DisplayName("validateメソッドが有効なパスワードの場合にエラーを返さない")
        void shouldNotReturnErrorWhenValidatingValidPassword() {
            // Given
            String password = "Password123!";

            // When
            List<ValidationError> errors = Password.validate(password);

            // Then
            assertTrue(errors.isEmpty());
        }

        @Test
        @DisplayName("reconstructメソッドで有効なパスワードで値オブジェクトを作成できる")
        void shouldReconstructPasswordWithValidPassword() {
            // Given
            String password = "Password123!";

            // When
            Password passwordVo = Password.reconstruct(password);

            // Then
            assertNotNull(passwordVo);
            assertEquals(password, passwordVo.value());
        }

        @Test
        @DisplayName("toStringメソッドがパスワードをマスクして返す")
        void shouldMaskPasswordInToString() {
            // Given
            String password = "Password123!";
            Password passwordVo = Password.reconstruct(password);

            // When
            String result = passwordVo.toString();

            // Then
            assertTrue(result.contains("***"));
            assertFalse(result.contains(password));
        }
    }

    @Nested
    @DisplayName("異常系")
    class AbnormalCase {
        @Test
        @DisplayName("nullでパスワードを作成しようとすると例外がスローされる")
        void shouldThrowExceptionWhenCreatingPasswordWithNull() {
            // When & Then
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> Password.create(null)
            );
            assertFalse(exception.getErrors().isEmpty());
            assertEquals("password", exception.getErrors().get(0).field());
        }

        @Test
        @DisplayName("空文字列でパスワードを作成しようとすると例外がスローされる")
        void shouldThrowExceptionWhenCreatingPasswordWithEmptyString() {
            // When & Then
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> Password.create("")
            );
            assertFalse(exception.getErrors().isEmpty());
            assertEquals("password", exception.getErrors().get(0).field());
        }

        @Test
        @DisplayName("7文字以下のパスワードで作成しようとすると例外がスローされる")
        void shouldThrowExceptionWhenCreatingPasswordWithLessThanMinimumLength() {
            // Given
            String password = "Pass12!";

            // When & Then
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> Password.create(password)
            );
            assertFalse(exception.getErrors().isEmpty());
            assertEquals("password", exception.getErrors().get(0).field());
            assertEquals("validation.password.length", exception.getErrors().get(0).messageKey());
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "password", // 小文字のみ
                "PASSWORD", // 大文字のみ
                "12345678", // 数字のみ
                "Password", // 大文字・小文字のみ（2種類）
                "password123" // 小文字・数字のみ（2種類）
        })
        @DisplayName("文字種が2種類以下のパスワードで作成しようとすると例外がスローされる")
        void shouldThrowExceptionWhenCreatingPasswordWithLessThanThreeCharacterTypes(String invalidPassword) {
            // When & Then
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> Password.create(invalidPassword)
            );
            assertFalse(exception.getErrors().isEmpty());
            assertEquals("password", exception.getErrors().get(0).field());
            assertEquals("validation.password.characterTypes", exception.getErrors().get(0).messageKey());
        }

        @Test
        @DisplayName("validateメソッドがnullの場合にエラーを返す")
        void shouldReturnErrorWhenValidatingNull() {
            // When
            List<ValidationError> errors = Password.validate(null);

            // Then
            assertFalse(errors.isEmpty());
            assertEquals("password", errors.get(0).field());
        }

        @Test
        @DisplayName("validateメソッドが空文字列の場合にエラーを返す")
        void shouldReturnErrorWhenValidatingEmptyString() {
            // When
            List<ValidationError> errors = Password.validate("");

            // Then
            assertFalse(errors.isEmpty());
            assertEquals("password", errors.get(0).field());
        }

        @Test
        @DisplayName("validateメソッドが7文字以下の場合にエラーを返す")
        void shouldReturnErrorWhenValidatingLessThanMinimumLength() {
            // Given
            String password = "Pass12!";

            // When
            List<ValidationError> errors = Password.validate(password);

            // Then
            assertFalse(errors.isEmpty());
            assertEquals("password", errors.get(0).field());
            assertEquals("validation.password.length", errors.get(0).messageKey());
        }

        @Test
        @DisplayName("validateメソッドが文字種が2種類以下の場合にエラーを返す")
        void shouldReturnErrorWhenValidatingLessThanThreeCharacterTypes() {
            // Given
            String password = "password";

            // When
            List<ValidationError> errors = Password.validate(password);

            // Then
            assertFalse(errors.isEmpty());
            assertEquals("password", errors.get(0).field());
            assertEquals("validation.password.characterTypes", errors.get(0).messageKey());
        }

        @Test
        @DisplayName("reconstructメソッドでnullを渡すとIllegalArgumentExceptionがスローされる")
        void shouldThrowIllegalArgumentExceptionWhenReconstructingWithNull() {
            // When & Then
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> Password.reconstruct(null)
            );
            assertEquals("Password cannot be null", exception.getMessage());
        }
    }
}
