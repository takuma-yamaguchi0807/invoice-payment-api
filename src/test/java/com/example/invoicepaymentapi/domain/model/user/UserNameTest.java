package com.example.invoicepaymentapi.domain.model.user;

import com.example.invoicepaymentapi.domain.exception.DomainValidationException;
import com.example.invoicepaymentapi.domain.exception.ValidationError;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 氏名値オブジェクトの単体テスト
 */
class UserNameTest {

    @Nested
    @DisplayName("正常系")
    class NormalCase {
        @Test
        @DisplayName("有効な氏名で値オブジェクトを作成できる")
        void shouldCreateUserNameWithValidValue() {
            // Given
            String name = "山田太郎";

            // When
            UserName userName = UserName.create(name);

            // Then
            assertNotNull(userName);
            assertEquals(name, userName.value());
        }

        @Test
        @DisplayName("最大長（255文字）の氏名で値オブジェクトを作成できる")
        void shouldCreateUserNameWithMaxLength() {
            // Given
            String name = "a".repeat(255);

            // When
            UserName userName = UserName.create(name);

            // Then
            assertNotNull(userName);
            assertEquals(name, userName.value());
        }

        @Test
        @DisplayName("validateメソッドが有効な氏名の場合にエラーを返さない")
        void shouldNotReturnErrorWhenValidatingValidUserName() {
            // Given
            String name = "山田太郎";

            // When
            List<ValidationError> errors = UserName.validate(name);

            // Then
            assertTrue(errors.isEmpty());
        }

        @Test
        @DisplayName("reconstructメソッドで有効な氏名で値オブジェクトを作成できる")
        void shouldReconstructUserNameWithValidValue() {
            // Given
            String name = "山田太郎";

            // When
            UserName userName = UserName.reconstruct(name);

            // Then
            assertNotNull(userName);
            assertEquals(name, userName.value());
        }
    }

    @Nested
    @DisplayName("異常系")
    class AbnormalCase {
        @Test
        @DisplayName("nullで氏名を作成しようとすると例外がスローされる")
        void shouldThrowExceptionWhenCreatingUserNameWithNull() {
            // When & Then
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> UserName.create(null)
            );
            assertFalse(exception.getErrors().isEmpty());
            assertEquals("userName", exception.getErrors().get(0).field());
        }

        @Test
        @DisplayName("空文字列で氏名を作成しようとすると例外がスローされる")
        void shouldThrowExceptionWhenCreatingUserNameWithEmptyString() {
            // When & Then
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> UserName.create("")
            );
            assertFalse(exception.getErrors().isEmpty());
            assertEquals("userName", exception.getErrors().get(0).field());
        }

        @Test
        @DisplayName("255文字を超える氏名で作成しようとすると例外がスローされる")
        void shouldThrowExceptionWhenCreatingUserNameExceedingMaxLength() {
            // Given
            String name = "a".repeat(256);

            // When & Then
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> UserName.create(name)
            );
            assertFalse(exception.getErrors().isEmpty());
            assertEquals("userName", exception.getErrors().get(0).field());
            assertEquals("validation.maxLength", exception.getErrors().get(0).messageKey());
        }

        @Test
        @DisplayName("validateメソッドがnullの場合にエラーを返す")
        void shouldReturnErrorWhenValidatingNull() {
            // When
            List<ValidationError> errors = UserName.validate(null);

            // Then
            assertFalse(errors.isEmpty());
            assertEquals("userName", errors.get(0).field());
        }

        @Test
        @DisplayName("validateメソッドが空文字列の場合にエラーを返す")
        void shouldReturnErrorWhenValidatingEmptyString() {
            // When
            List<ValidationError> errors = UserName.validate("");

            // Then
            assertFalse(errors.isEmpty());
            assertEquals("userName", errors.get(0).field());
        }

        @Test
        @DisplayName("validateメソッドが255文字を超える場合にエラーを返す")
        void shouldReturnErrorWhenValidatingExceedingMaxLength() {
            // Given
            String name = "a".repeat(256);

            // When
            List<ValidationError> errors = UserName.validate(name);

            // Then
            assertFalse(errors.isEmpty());
            assertEquals("userName", errors.get(0).field());
            assertEquals("validation.maxLength", errors.get(0).messageKey());
        }

        @Test
        @DisplayName("reconstructメソッドでnullを渡すとIllegalArgumentExceptionがスローされる")
        void shouldThrowIllegalArgumentExceptionWhenReconstructingWithNull() {
            // When & Then
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> UserName.reconstruct(null)
            );
            assertEquals("UserName cannot be null", exception.getMessage());
        }
    }
}
