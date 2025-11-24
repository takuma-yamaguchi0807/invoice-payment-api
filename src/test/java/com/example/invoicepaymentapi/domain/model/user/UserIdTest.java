package com.example.invoicepaymentapi.domain.model.user;

import com.example.invoicepaymentapi.domain.exception.DomainValidationException;
import com.example.invoicepaymentapi.domain.exception.ValidationError;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ユーザーID値オブジェクトの単体テスト
 */
class UserIdTest {

    @Nested
    @DisplayName("正常系")
    class NormalCase {
        @Test
        @DisplayName("有効なユーザーID（1以上）で値オブジェクトを作成できる")
        void shouldCreateUserIdWithValidValue() {
            // Given
            Integer userId = 1;

            // When
            UserId userIdVo = UserId.create(userId);

            // Then
            assertNotNull(userIdVo);
            assertEquals(1, userIdVo.value());
        }

        @Test
        @DisplayName("validateメソッドが有効な値の場合にエラーを返さない")
        void shouldNotReturnErrorWhenValidatingValidValue() {
            // When
            List<ValidationError> errors = UserId.validate(1);

            // Then
            assertTrue(errors.isEmpty());
        }

        @Test
        @DisplayName("reconstructメソッドで有効な値で値オブジェクトを作成できる")
        void shouldReconstructUserIdWithValidValue() {
            // Given
            Integer value = 1;

            // When
            UserId userId = UserId.reconstruct(value);

            // Then
            assertNotNull(userId);
            assertEquals(1, userId.value());
        }
    }

    @Nested
    @DisplayName("異常系")
    class AbnormalCase {
        @Test
        @DisplayName("0でユーザーIDを作成しようとすると例外がスローされる")
        void shouldThrowExceptionWhenCreatingUserIdWithZero() {
            // When & Then
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> UserId.create(0)
            );
            assertFalse(exception.getErrors().isEmpty());
            assertEquals("userId", exception.getErrors().get(0).field());
            assertEquals("validation.userId.zeroOrNegative", exception.getErrors().get(0).messageKey());
        }

        @Test
        @DisplayName("負の値でユーザーIDを作成しようとすると例外がスローされる")
        void shouldThrowExceptionWhenCreatingUserIdWithNegativeValue() {
            // When & Then
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> UserId.create(-1)
            );
            assertFalse(exception.getErrors().isEmpty());
            assertEquals("userId", exception.getErrors().get(0).field());
            assertEquals("validation.userId.zeroOrNegative", exception.getErrors().get(0).messageKey());
        }

        @Test
        @DisplayName("nullでユーザーIDを作成しようとすると例外がスローされる")
        void shouldThrowExceptionWhenCreatingUserIdWithNull() {
            // When & Then
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> UserId.create(null)
            );
            assertFalse(exception.getErrors().isEmpty());
            assertEquals("userId", exception.getErrors().get(0).field());
        }

        @Test
        @DisplayName("validateメソッドがnullの場合にエラーを返す")
        void shouldReturnErrorWhenValidatingNull() {
            // When
            List<ValidationError> errors = UserId.validate(null);

            // Then
            assertFalse(errors.isEmpty());
            assertEquals("userId", errors.get(0).field());
        }

        @Test
        @DisplayName("validateメソッドが0の場合にエラーを返す")
        void shouldReturnErrorWhenValidatingZero() {
            // When
            List<ValidationError> errors = UserId.validate(0);

            // Then
            assertFalse(errors.isEmpty());
            assertEquals("userId", errors.get(0).field());
            assertEquals("validation.userId.zeroOrNegative", errors.get(0).messageKey());
        }

        @Test
        @DisplayName("validateメソッドが負の値の場合にエラーを返す")
        void shouldReturnErrorWhenValidatingNegativeValue() {
            // When
            List<ValidationError> errors = UserId.validate(-1);

            // Then
            assertFalse(errors.isEmpty());
            assertEquals("userId", errors.get(0).field());
            assertEquals("validation.userId.zeroOrNegative", errors.get(0).messageKey());
        }

        @Test
        @DisplayName("reconstructメソッドでnullを渡すとIllegalArgumentExceptionがスローされる")
        void shouldThrowIllegalArgumentExceptionWhenReconstructingWithNull() {
            // When & Then
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> UserId.reconstruct(null)
            );
            assertEquals("UserId cannot be null", exception.getMessage());
        }
    }
}
