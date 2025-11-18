package com.example.invoicepaymentapi.domain.model.user;

import com.example.invoicepaymentapi.domain.exception.DomainValidationException;
import com.example.invoicepaymentapi.domain.exception.ValidationError;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * メールアドレス値オブジェクトの単体テスト
 */
class EmailTest {

    @Nested
    @DisplayName("正常系")
    class NormalCase {
        @Test
        @DisplayName("有効なメールアドレスで値オブジェクトを作成できる")
        void shouldCreateEmailWithValidAddress() {
            // Given
            String email = "test@example.com";

            // When
            Email emailVo = Email.create(email);

            // Then
            assertNotNull(emailVo);
            assertEquals(email, emailVo.value());
        }

        @Test
        @DisplayName("最大長（255文字）のメールアドレスで値オブジェクトを作成できる")
        void shouldCreateEmailWithMaxLength() {
            // Given
            String email = "a".repeat(245) + "@example.com"; // 255文字

            // When
            Email emailVo = Email.create(email);

            // Then
            assertNotNull(emailVo);
            assertEquals(email, emailVo.value());
        }

        @Test
        @DisplayName("validateメソッドが有効なメールアドレスの場合にエラーを返さない")
        void shouldNotReturnErrorWhenValidatingValidEmail() {
            // Given
            String email = "test@example.com";

            // When
            List<ValidationError> errors = Email.validate(email);

            // Then
            assertTrue(errors.isEmpty());
        }

        @Test
        @DisplayName("reconstructメソッドでnullでも値オブジェクトを作成できる")
        void shouldReconstructEmailWithNull() {
            // When
            Email email = Email.reconstruct(null);

            // Then
            assertNotNull(email);
            assertNull(email.value());
        }

        @Test
        @DisplayName("reconstructメソッドで有効なメールアドレスで値オブジェクトを作成できる")
        void shouldReconstructEmailWithValidAddress() {
            // Given
            String email = "test@example.com";

            // When
            Email emailVo = Email.reconstruct(email);

            // Then
            assertNotNull(emailVo);
            assertEquals(email, emailVo.value());
        }
    }

    @Nested
    @DisplayName("異常系")
    class AbnormalCase {
        @Test
        @DisplayName("nullでメールアドレスを作成しようとすると例外がスローされる")
        void shouldThrowExceptionWhenCreatingEmailWithNull() {
            // When & Then
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> Email.create(null)
            );
            assertFalse(exception.getErrors().isEmpty());
            assertEquals("email", exception.getErrors().get(0).field());
        }

        @Test
        @DisplayName("空文字列でメールアドレスを作成しようとすると例外がスローされる")
        void shouldThrowExceptionWhenCreatingEmailWithEmptyString() {
            // When & Then
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> Email.create("")
            );
            assertFalse(exception.getErrors().isEmpty());
            assertEquals("email", exception.getErrors().get(0).field());
        }

        @Test
        @DisplayName("255文字を超えるメールアドレスで作成しようとすると例外がスローされる")
        void shouldThrowExceptionWhenCreatingEmailExceedingMaxLength() {
            // Given
            String email = "a".repeat(246) + "@example.com"; // 256文字

            // When & Then
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> Email.create(email)
            );
            assertFalse(exception.getErrors().isEmpty());
            assertEquals("email", exception.getErrors().get(0).field());
            assertEquals("validation.email.maxLength", exception.getErrors().get(0).messageKey());
        }

        @Test
        @DisplayName("不正な形式のメールアドレスで作成しようとすると例外がスローされる")
        void shouldThrowExceptionWhenCreatingEmailWithInvalidFormat() {
            // Given
            String[] invalidEmails = {
                    "invalid-email",
                    "@example.com",
                    "test@",
                    "test@example",
                    "test..test@example.com"
            };

            for (String invalidEmail : invalidEmails) {
                // When & Then
                DomainValidationException exception = assertThrows(
                        DomainValidationException.class,
                        () -> Email.create(invalidEmail)
                );
                assertFalse(exception.getErrors().isEmpty());
                assertEquals("email", exception.getErrors().get(0).field());
                assertEquals("validation.email.format", exception.getErrors().get(0).messageKey());
            }
        }

        @Test
        @DisplayName("validateメソッドがnullの場合にエラーを返す")
        void shouldReturnErrorWhenValidatingNull() {
            // When
            List<ValidationError> errors = Email.validate(null);

            // Then
            assertFalse(errors.isEmpty());
            assertEquals("email", errors.get(0).field());
        }

        @Test
        @DisplayName("validateメソッドが空文字列の場合にエラーを返す")
        void shouldReturnErrorWhenValidatingEmptyString() {
            // When
            List<ValidationError> errors = Email.validate("");

            // Then
            assertFalse(errors.isEmpty());
            assertEquals("email", errors.get(0).field());
        }

        @Test
        @DisplayName("validateメソッドが255文字を超える場合にエラーを返す")
        void shouldReturnErrorWhenValidatingExceedingMaxLength() {
            // Given
            String email = "a".repeat(246) + "@example.com"; // 256文字

            // When
            List<ValidationError> errors = Email.validate(email);

            // Then
            assertFalse(errors.isEmpty());
            assertEquals("email", errors.get(0).field());
            assertEquals("validation.email.maxLength", errors.get(0).messageKey());
        }

        @Test
        @DisplayName("validateメソッドが不正な形式の場合にエラーを返す")
        void shouldReturnErrorWhenValidatingInvalidFormat() {
            // Given
            String email = "invalid-email";

            // When
            List<ValidationError> errors = Email.validate(email);

            // Then
            assertFalse(errors.isEmpty());
            assertEquals("email", errors.get(0).field());
            assertEquals("validation.email.format", errors.get(0).messageKey());
        }
    }
}
