package com.example.invoicepaymentapi.domain.model.invoice;

import com.example.invoicepaymentapi.domain.exception.DomainValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 支払期日範囲値オブジェクトの単体テスト
 */
class PaymentDueDateRangeTest {

    @Nested
    @DisplayName("正常系")
    class NormalCase {
        @Test
        @DisplayName("両方の日付を指定して支払期日範囲を作成できる")
        void shouldCreatePaymentDueDateRangeWithBothDates() {
            // Given
            String fromString = LocalDate.now().plusDays(10).toString();
            String toString = LocalDate.now().plusDays(40).toString();
            PaymentDueDate from = PaymentDueDate.create(fromString);
            PaymentDueDate to = PaymentDueDate.create(toString);

            // When
            PaymentDueDateRange range = PaymentDueDateRange.create(from, to);

            // Then
            assertNotNull(range);
            assertEquals(LocalDate.parse(fromString), range.from().value());
            assertEquals(LocalDate.parse(toString), range.to().value());
        }

        @Test
        @DisplayName("fromがnullの場合、デフォルト（明日）が使われる")
        void shouldUseDefaultFromWhenFromIsNull() {
            // Given
            String toString = LocalDate.now().plusDays(40).toString();
            PaymentDueDate from = PaymentDueDate.ofCreateOrDefaultFrom(null);
            PaymentDueDate to = PaymentDueDate.create(toString);

            // When
            PaymentDueDateRange range = PaymentDueDateRange.create(from, to);

            // Then
            assertNotNull(range);
            assertEquals(LocalDate.now().plusDays(1), range.from().value());
            assertEquals(LocalDate.parse(toString), range.to().value());
        }

        @Test
        @DisplayName("fromが空文字列の場合、デフォルト（明日）が使われる")
        void shouldUseDefaultFromWhenFromIsEmpty() {
            // Given
            String toString = LocalDate.now().plusDays(40).toString();
            PaymentDueDate from = PaymentDueDate.ofCreateOrDefaultFrom("");
            PaymentDueDate to = PaymentDueDate.create(toString);

            // When
            PaymentDueDateRange range = PaymentDueDateRange.create(from, to);

            // Then
            assertNotNull(range);
            assertEquals(LocalDate.now().plusDays(1), range.from().value());
            assertEquals(LocalDate.parse(toString), range.to().value());
        }

        @Test
        @DisplayName("toがnullの場合、fromから1ヶ月後が使われる")
        void shouldUseDefaultToWhenToIsNull() {
            // Given
            LocalDate fromDate = LocalDate.now().plusDays(10);
            String fromString = fromDate.toString();
            PaymentDueDate from = PaymentDueDate.create(fromString);
            PaymentDueDate to = PaymentDueDate.ofCreateOrDefaultTo(null, from);

            // When
            PaymentDueDateRange range = PaymentDueDateRange.create(from, to);

            // Then
            assertNotNull(range);
            assertEquals(fromDate, range.from().value());
            assertEquals(fromDate.plusMonths(1), range.to().value());
        }

        @Test
        @DisplayName("toが空文字列の場合、fromから1ヶ月後が使われる")
        void shouldUseDefaultToWhenToIsEmpty() {
            // Given
            LocalDate fromDate = LocalDate.now().plusDays(10);
            String fromString = fromDate.toString();
            PaymentDueDate from = PaymentDueDate.create(fromString);
            PaymentDueDate to = PaymentDueDate.ofCreateOrDefaultTo("", from);

            // When
            PaymentDueDateRange range = PaymentDueDateRange.create(from, to);

            // Then
            assertNotNull(range);
            assertEquals(fromDate, range.from().value());
            assertEquals(fromDate.plusMonths(1), range.to().value());
        }

        @Test
        @DisplayName("両方nullの場合、デフォルト値が使われる")
        void shouldUseDefaultValuesWhenBothAreNull() {
            // When
            PaymentDueDate from = PaymentDueDate.ofCreateOrDefaultFrom(null);
            PaymentDueDate to = PaymentDueDate.ofCreateOrDefaultTo(null, from);
            PaymentDueDateRange range = PaymentDueDateRange.create(from, to);

            // Then
            assertNotNull(range);
            LocalDate expectedFrom = LocalDate.now().plusDays(1);
            assertEquals(expectedFrom, range.from().value());
            assertEquals(expectedFrom.plusMonths(1), range.to().value());
        }

        @Test
        @DisplayName("fromとtoが同じ日付の場合、作成できる")
        void shouldCreatePaymentDueDateRangeWhenFromEqualsTo() {
            // Given
            String dateString = LocalDate.now().plusDays(10).toString();
            PaymentDueDate date = PaymentDueDate.create(dateString);

            // When
            PaymentDueDateRange range = PaymentDueDateRange.create(date, date);

            // Then
            assertNotNull(range);
            assertEquals(LocalDate.parse(dateString), range.from().value());
            assertEquals(LocalDate.parse(dateString), range.to().value());
        }
    }

    @Nested
    @DisplayName("異常系 - 開始日のバリデーション")
    class AbnormalCaseFrom {
        @Test
        @DisplayName("fromが不正な日付形式の場合、例外がスローされる")
        void shouldThrowExceptionWhenFromHasInvalidFormat() {
            // Given
            String invalidFrom = "2024/01/01";
            String validTo = LocalDate.now().plusDays(40).toString();

            // When & Then
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> {
                        PaymentDueDate from = PaymentDueDate.create(invalidFrom);
                        PaymentDueDate to = PaymentDueDate.create(validTo);
                        PaymentDueDateRange.create(from, to);
                    }
            );
            assertFalse(exception.getErrors().isEmpty());
            assertEquals("paymentDueDate", exception.getErrors().get(0).field());
            assertEquals("validation.date.format", exception.getErrors().get(0).messageKey());
        }

        @Test
        @DisplayName("fromが今日の日付の場合、例外がスローされる")
        void shouldThrowExceptionWhenFromIsToday() {
            // Given
            String today = LocalDate.now().toString();
            String validTo = LocalDate.now().plusDays(40).toString();

            // When & Then
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> {
                        PaymentDueDate from = PaymentDueDate.create(today);
                        PaymentDueDate to = PaymentDueDate.create(validTo);
                        PaymentDueDateRange.create(from, to);
                    }
            );
            assertFalse(exception.getErrors().isEmpty());
            assertEquals("paymentDueDate", exception.getErrors().get(0).field());
            assertEquals("validation.paymentDueDate.notFuture", exception.getErrors().get(0).messageKey());
        }

        @Test
        @DisplayName("fromが過去の日付の場合、例外がスローされる")
        void shouldThrowExceptionWhenFromIsPast() {
            // Given
            String pastDate = LocalDate.now().minusDays(1).toString();
            String validTo = LocalDate.now().plusDays(40).toString();

            // When & Then
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> {
                        PaymentDueDate from = PaymentDueDate.create(pastDate);
                        PaymentDueDate to = PaymentDueDate.create(validTo);
                        PaymentDueDateRange.create(from, to);
                    }
            );
            assertFalse(exception.getErrors().isEmpty());
            assertEquals("paymentDueDate", exception.getErrors().get(0).field());
            assertEquals("validation.paymentDueDate.notFuture", exception.getErrors().get(0).messageKey());
        }
    }

    @Nested
    @DisplayName("異常系 - 終了日のバリデーション")
    class AbnormalCaseTo {
        @Test
        @DisplayName("toが不正な日付形式の場合、例外がスローされる")
        void shouldThrowExceptionWhenToHasInvalidFormat() {
            // Given
            String validFrom = LocalDate.now().plusDays(10).toString();
            String invalidTo = "2024/01/01";

            // When & Then
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> {
                        PaymentDueDate from = PaymentDueDate.create(validFrom);
                        PaymentDueDate to = PaymentDueDate.create(invalidTo);
                        PaymentDueDateRange.create(from, to);
                    }
            );
            assertFalse(exception.getErrors().isEmpty());
            assertEquals("paymentDueDate", exception.getErrors().get(0).field());
            assertEquals("validation.date.format", exception.getErrors().get(0).messageKey());
        }

        @Test
        @DisplayName("toが今日の日付の場合、例外がスローされる")
        void shouldThrowExceptionWhenToIsToday() {
            // Given
            String validFrom = LocalDate.now().plusDays(10).toString();
            String today = LocalDate.now().toString();

            // When & Then
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> {
                        PaymentDueDate from = PaymentDueDate.create(validFrom);
                        PaymentDueDate to = PaymentDueDate.create(today);
                        PaymentDueDateRange.create(from, to);
                    }
            );
            assertFalse(exception.getErrors().isEmpty());
            assertEquals("paymentDueDate", exception.getErrors().get(0).field());
            assertEquals("validation.paymentDueDate.notFuture", exception.getErrors().get(0).messageKey());
        }

        @Test
        @DisplayName("toが過去の日付の場合、例外がスローされる")
        void shouldThrowExceptionWhenToIsPast() {
            // Given
            String validFrom = LocalDate.now().plusDays(10).toString();
            String pastDate = LocalDate.now().minusDays(1).toString();

            // When & Then
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> {
                        PaymentDueDate from = PaymentDueDate.create(validFrom);
                        PaymentDueDate to = PaymentDueDate.create(pastDate);
                        PaymentDueDateRange.create(from, to);
                    }
            );
            assertFalse(exception.getErrors().isEmpty());
            assertEquals("paymentDueDate", exception.getErrors().get(0).field());
            assertEquals("validation.paymentDueDate.notFuture", exception.getErrors().get(0).messageKey());
        }
    }

    @Nested
    @DisplayName("異常系 - 日付範囲のバリデーション")
    class AbnormalCaseRange {
        @Test
        @DisplayName("fromがtoより後の場合、例外がスローされる")
        void shouldThrowExceptionWhenFromIsAfterTo() {
            // Given
            String fromString = LocalDate.now().plusDays(40).toString();
            String toString = LocalDate.now().plusDays(10).toString();
            PaymentDueDate from = PaymentDueDate.create(fromString);
            PaymentDueDate to = PaymentDueDate.create(toString);

            // When & Then
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> PaymentDueDateRange.create(from, to)
            );
            assertFalse(exception.getErrors().isEmpty());
            assertEquals("paymentDueTo", exception.getErrors().get(0).field());
            assertEquals("validation.paymentDueTo.range", exception.getErrors().get(0).messageKey());
        }
    }
}

