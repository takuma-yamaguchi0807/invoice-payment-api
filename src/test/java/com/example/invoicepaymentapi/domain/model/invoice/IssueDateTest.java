package com.example.invoicepaymentapi.domain.model.invoice;

import com.example.invoicepaymentapi.domain.exception.DomainValidationException;
import com.example.invoicepaymentapi.domain.exception.ValidationError;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 発行日値オブジェクトの単体テスト
 */
class IssueDateTest {

    @Nested
    @DisplayName("正常系")
    class NormalCase {
        @Test
        @DisplayName("有効な日付文字列から発行日を作成できる")
        void shouldCreateIssueDateFromString() {
            // Given
            String dateString = LocalDate.now().toString();

            // When
            IssueDate issueDate = IssueDate.create(dateString);

            // Then
            assertNotNull(issueDate);
            assertEquals(LocalDate.parse(dateString), issueDate.value());
        }

        @Test
        @DisplayName("validateメソッドが有効な過去の日付の場合にエラーを返さない")
        void shouldNotReturnErrorWhenValidatingPastDate() {
            // Given
            String pastDate = LocalDate.now().minusDays(1).toString();

            // When
            List<ValidationError> errors = IssueDate.validate(pastDate);

            // Then
            assertTrue(errors.isEmpty());
        }

        @Test
        @DisplayName("reconstructメソッドで有効な日付で値オブジェクトを作成できる")
        void shouldReconstructIssueDateWithValidDate() {
            // Given
            LocalDate date = LocalDate.now();

            // When
            IssueDate issueDate = IssueDate.reconstruct(date);

            // Then
            assertNotNull(issueDate);
            assertEquals(date, issueDate.value());
        }
    }

    @Nested
    @DisplayName("異常系")
    class AbnormalCase {
        @Test
        @DisplayName("空文字列で発行日を作成しようとすると例外がスローされる")
        void shouldThrowExceptionWhenCreatingIssueDateWithEmptyString() {
            // When & Then
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> IssueDate.create("")
            );
            assertFalse(exception.getErrors().isEmpty());
            assertEquals("issueDate", exception.getErrors().get(0).field());
        }

        @Test
        @DisplayName("不正な日付形式で発行日を作成しようとすると例外がスローされる")
        void shouldThrowExceptionWhenCreatingIssueDateWithInvalidFormat() {
            // When & Then
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> IssueDate.create("2024/01/01")
            );
            assertFalse(exception.getErrors().isEmpty());
            assertEquals("issueDate", exception.getErrors().get(0).field());
            assertEquals("validation.date.format", exception.getErrors().get(0).messageKey());
        }

        @Test
        @DisplayName("validateメソッドがnullの場合にエラーを返す")
        void shouldReturnErrorWhenValidatingNull() {
            // When
            List<ValidationError> errors = IssueDate.validate(null);

            // Then
            assertFalse(errors.isEmpty());
            assertEquals("issueDate", errors.get(0).field());
        }

        @Test
        @DisplayName("validateメソッドが空文字列の場合にエラーを返す")
        void shouldReturnErrorWhenValidatingEmptyString() {
            // When
            List<ValidationError> errors = IssueDate.validate("");

            // Then
            assertFalse(errors.isEmpty());
            assertEquals("issueDate", errors.get(0).field());
        }

        @Test
        @DisplayName("validateメソッドが未来の日付の場合にエラーを返す")
        void shouldReturnErrorWhenValidatingFutureDate() {
            // Given
            String futureDate = LocalDate.now().plusDays(1).toString();

            // When
            List<ValidationError> errors = IssueDate.validate(futureDate);

            // Then
            assertFalse(errors.isEmpty());
            assertEquals("issueDate", errors.get(0).field());
            assertEquals("validation.issueDate.future", errors.get(0).messageKey());
        }

        @Test
        @DisplayName("reconstructメソッドでnullを渡すとIllegalArgumentExceptionがスローされる")
        void shouldThrowIllegalArgumentExceptionWhenReconstructingWithNull() {
            // When & Then
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> IssueDate.reconstruct(null)
            );
            assertEquals("IssueDate cannot be null", exception.getMessage());
        }
    }
}
