package com.example.invoicepaymentapi.domain.service;

import com.example.invoicepaymentapi.domain.exception.DomainValidationException;
import com.example.invoicepaymentapi.domain.exception.ValidationError;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ドメイン層のバリデーションサービスの単体テスト
 */
class DomainValidationServiceTest {

    @Nested
    @DisplayName("正常系")
    class NormalCase {
        @Test
        @DisplayName("すべてのバリデーションが成功した場合、例外がスローされない")
        void shouldNotThrowExceptionWhenAllValidationsPass() {
            // Given
            Supplier<List<ValidationError>> validator1 = () -> new ArrayList<>();
            Supplier<List<ValidationError>> validator2 = () -> new ArrayList<>();

            // When & Then
            assertDoesNotThrow(() -> {
                DomainValidationService.validateAll(validator1, validator2);
            });
        }

        @Test
        @DisplayName("バリデーションが0件の場合、例外がスローされない")
        void shouldNotThrowExceptionWhenNoValidatorsProvided() {
            // When & Then
            assertDoesNotThrow(() -> {
                DomainValidationService.validateAll();
            });
        }
    }

    @Nested
    @DisplayName("異常系")
    class AbnormalCase {
        @Test
        @DisplayName("1つのバリデーションがエラーを返した場合、例外がスローされる")
        void shouldThrowExceptionWhenOneValidationFails() {
            // Given
            Supplier<List<ValidationError>> validator1 = () -> new ArrayList<>();
            Supplier<List<ValidationError>> validator2 = () -> List.of(
                    new ValidationError("field1", "validation.error")
            );

            // When & Then
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> DomainValidationService.validateAll(validator1, validator2)
            );
            assertFalse(exception.getErrors().isEmpty());
            assertEquals(1, exception.getErrors().size());
            assertEquals("field1", exception.getErrors().get(0).field());
        }

        @Test
        @DisplayName("複数のバリデーションがエラーを返した場合、すべてのエラーが集約される")
        void shouldAggregateAllErrorsWhenMultipleValidationsFail() {
            // Given
            Supplier<List<ValidationError>> validator1 = () -> List.of(
                    new ValidationError("field1", "validation.error1")
            );
            Supplier<List<ValidationError>> validator2 = () -> List.of(
                    new ValidationError("field2", "validation.error2"),
                    new ValidationError("field3", "validation.error3")
            );

            // When & Then
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> DomainValidationService.validateAll(validator1, validator2)
            );
            assertFalse(exception.getErrors().isEmpty());
            assertEquals(3, exception.getErrors().size());
        }

        @Test
        @DisplayName("同じフィールドで複数のエラーがある場合、すべてのエラーが集約される")
        void shouldAggregateMultipleErrorsForSameField() {
            // Given
            Supplier<List<ValidationError>> validator1 = () -> List.of(
                    new ValidationError("field1", "validation.error1")
            );
            Supplier<List<ValidationError>> validator2 = () -> List.of(
                    new ValidationError("field1", "validation.error2")
            );

            // When & Then
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> DomainValidationService.validateAll(validator1, validator2)
            );
            assertFalse(exception.getErrors().isEmpty());
            assertEquals(2, exception.getErrors().size());
            assertEquals("field1", exception.getErrors().get(0).field());
            assertEquals("field1", exception.getErrors().get(1).field());
        }
    }
}
