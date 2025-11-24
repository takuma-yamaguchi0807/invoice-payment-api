package com.example.invoicepaymentapi.domain.shared.pagination;

import com.example.invoicepaymentapi.domain.exception.DomainValidationException;
import com.example.invoicepaymentapi.domain.exception.ValidationError;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ページ番号値オブジェクトの単体テスト
 */
class PageNumberTest {

    @Nested
    @DisplayName("正常系")
    class NormalCase {
        @Test
        @DisplayName("有効なページ番号（1以上）で値オブジェクトを作成できる")
        void shouldCreatePageNumberWithValidValue() {
            // Given
            Integer pageNumber = 1;

            // When
            PageNumber pageNumberVo = PageNumber.create(pageNumber);

            // Then
            assertNotNull(pageNumberVo);
            assertEquals(1, pageNumberVo.value());
        }

        @Test
        @DisplayName("デフォルト値でページ番号を作成できる")
        void shouldCreatePageNumberWithDefaultValue() {
            // When
            PageNumber pageNumber = PageNumber.defaultValue();

            // Then
            assertNotNull(pageNumber);
            assertEquals(PageNumber.DEFAULT_VALUE, pageNumber.value());
        }

        @Test
        @DisplayName("nullの場合はデフォルト値でページ番号を作成できる")
        void shouldCreatePageNumberWithDefaultWhenNull() {
            // When
            PageNumber pageNumber = PageNumber.ofCreateOrDefault(null);

            // Then
            assertNotNull(pageNumber);
            assertEquals(PageNumber.DEFAULT_VALUE, pageNumber.value());
        }

        @Test
        @DisplayName("有効な値の場合はその値でページ番号を作成できる")
        void shouldCreatePageNumberWithValueWhenNotNull() {
            // Given
            Integer value = 5;

            // When
            PageNumber pageNumber = PageNumber.ofCreateOrDefault(value);

            // Then
            assertNotNull(pageNumber);
            assertEquals(5, pageNumber.value());
        }

        @Test
        @DisplayName("validateメソッドが有効な値の場合にエラーを返さない")
        void shouldNotReturnErrorWhenValidatingValidValue() {
            // When
            List<ValidationError> errors = PageNumber.validate(1);

            // Then
            assertTrue(errors.isEmpty());
        }

        @Test
        @DisplayName("reconstructメソッドでnullでも値オブジェクトを作成できる")
        void shouldReconstructPageNumberWithNull() {
            // When
            PageNumber pageNumber = PageNumber.reconstruct(null);

            // Then
            assertNotNull(pageNumber);
            assertNull(pageNumber.value());
        }

        @Test
        @DisplayName("reconstructメソッドで有効な値で値オブジェクトを作成できる")
        void shouldReconstructPageNumberWithValidValue() {
            // Given
            Integer value = 5;

            // When
            PageNumber pageNumber = PageNumber.reconstruct(value);

            // Then
            assertNotNull(pageNumber);
            assertEquals(5, pageNumber.value());
        }
    }

    @Nested
    @DisplayName("異常系")
    class AbnormalCase {
        @Test
        @DisplayName("0でページ番号を作成しようとすると例外がスローされる")
        void shouldThrowExceptionWhenCreatingPageNumberWithZero() {
            // When & Then
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> PageNumber.create(0)
            );
            assertFalse(exception.getErrors().isEmpty());
            assertEquals("page_number", exception.getErrors().get(0).field());
            assertEquals("validation.pageNumber.min", exception.getErrors().get(0).messageKey());
        }

        @Test
        @DisplayName("負の値でページ番号を作成しようとすると例外がスローされる")
        void shouldThrowExceptionWhenCreatingPageNumberWithNegativeValue() {
            // When & Then
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> PageNumber.create(-1)
            );
            assertFalse(exception.getErrors().isEmpty());
            assertEquals("page_number", exception.getErrors().get(0).field());
            assertEquals("validation.pageNumber.min", exception.getErrors().get(0).messageKey());
        }

        @Test
        @DisplayName("nullでページ番号を作成しようとすると例外がスローされる")
        void shouldThrowExceptionWhenCreatingPageNumberWithNull() {
            // When & Then
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> PageNumber.create(null)
            );
            assertFalse(exception.getErrors().isEmpty());
            assertEquals("page_number", exception.getErrors().get(0).field());
        }

        @Test
        @DisplayName("validateメソッドがnullの場合にエラーを返す")
        void shouldReturnErrorWhenValidatingNull() {
            // When
            List<ValidationError> errors = PageNumber.validate(null);

            // Then
            assertFalse(errors.isEmpty());
            assertEquals("page_number", errors.get(0).field());
        }

        @Test
        @DisplayName("validateメソッドが0の場合にエラーを返す")
        void shouldReturnErrorWhenValidatingZero() {
            // When
            List<ValidationError> errors = PageNumber.validate(0);

            // Then
            assertFalse(errors.isEmpty());
            assertEquals("page_number", errors.get(0).field());
            assertEquals("validation.pageNumber.min", errors.get(0).messageKey());
        }

        @Test
        @DisplayName("validateメソッドが負の値の場合にエラーを返す")
        void shouldReturnErrorWhenValidatingNegativeValue() {
            // When
            List<ValidationError> errors = PageNumber.validate(-1);

            // Then
            assertFalse(errors.isEmpty());
            assertEquals("page_number", errors.get(0).field());
            assertEquals("validation.pageNumber.min", errors.get(0).messageKey());
        }
    }
}
