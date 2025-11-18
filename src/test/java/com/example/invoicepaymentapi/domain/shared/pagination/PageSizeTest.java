package com.example.invoicepaymentapi.domain.shared.pagination;

import com.example.invoicepaymentapi.domain.exception.DomainValidationException;
import com.example.invoicepaymentapi.domain.exception.ValidationError;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ページサイズ値オブジェクトの単体テスト
 */
class PageSizeTest {

    @Nested
    @DisplayName("正常系")
    class NormalCase {
        @Test
        @DisplayName("有効なページサイズ（1以上100以下）で値オブジェクトを作成できる")
        void shouldCreatePageSizeWithValidValue() {
            // Given
            Integer pageSize = 20;

            // When
            PageSize pageSizeVo = PageSize.create(pageSize);

            // Then
            assertNotNull(pageSizeVo);
            assertEquals(20, pageSizeVo.value());
        }

        @Test
        @DisplayName("最小値（1）でページサイズを作成できる")
        void shouldCreatePageSizeWithMinimumValue() {
            // Given
            Integer pageSize = 1;

            // When
            PageSize pageSizeVo = PageSize.create(pageSize);

            // Then
            assertNotNull(pageSizeVo);
            assertEquals(1, pageSizeVo.value());
        }

        @Test
        @DisplayName("最大値（100）でページサイズを作成できる")
        void shouldCreatePageSizeWithMaximumValue() {
            // Given
            Integer pageSize = 100;

            // When
            PageSize pageSizeVo = PageSize.create(pageSize);

            // Then
            assertNotNull(pageSizeVo);
            assertEquals(100, pageSizeVo.value());
        }

        @Test
        @DisplayName("デフォルト値でページサイズを作成できる")
        void shouldCreatePageSizeWithDefaultValue() {
            // When
            PageSize pageSize = PageSize.defaultValue();

            // Then
            assertNotNull(pageSize);
            assertEquals(PageSize.DEFAULT_VALUE, pageSize.value());
        }

        @Test
        @DisplayName("nullの場合はデフォルト値でページサイズを作成できる")
        void shouldCreatePageSizeWithDefaultWhenNull() {
            // When
            PageSize pageSize = PageSize.ofCreateOrDefault(null);

            // Then
            assertNotNull(pageSize);
            assertEquals(PageSize.DEFAULT_VALUE, pageSize.value());
        }

        @Test
        @DisplayName("有効な値の場合はその値でページサイズを作成できる")
        void shouldCreatePageSizeWithValueWhenNotNull() {
            // Given
            Integer value = 50;

            // When
            PageSize pageSize = PageSize.ofCreateOrDefault(value);

            // Then
            assertNotNull(pageSize);
            assertEquals(50, pageSize.value());
        }

        @Test
        @DisplayName("validateメソッドが有効な値の場合にエラーを返さない")
        void shouldNotReturnErrorWhenValidatingValidValue() {
            // When
            List<ValidationError> errors = PageSize.validate(20);

            // Then
            assertTrue(errors.isEmpty());
        }

        @Test
        @DisplayName("reconstructメソッドでnullでも値オブジェクトを作成できる")
        void shouldReconstructPageSizeWithNull() {
            // When
            PageSize pageSize = PageSize.reconstruct(null);

            // Then
            assertNotNull(pageSize);
            assertNull(pageSize.value());
        }

        @Test
        @DisplayName("reconstructメソッドで有効な値で値オブジェクトを作成できる")
        void shouldReconstructPageSizeWithValidValue() {
            // Given
            Integer value = 50;

            // When
            PageSize pageSize = PageSize.reconstruct(value);

            // Then
            assertNotNull(pageSize);
            assertEquals(50, pageSize.value());
        }
    }

    @Nested
    @DisplayName("異常系")
    class AbnormalCase {
        @Test
        @DisplayName("0でページサイズを作成しようとすると例外がスローされる")
        void shouldThrowExceptionWhenCreatingPageSizeWithZero() {
            // When & Then
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> PageSize.create(0)
            );
            assertFalse(exception.getErrors().isEmpty());
            assertEquals("page_size", exception.getErrors().get(0).field());
            assertEquals("validation.pageSize.min", exception.getErrors().get(0).messageKey());
        }

        @Test
        @DisplayName("負の値でページサイズを作成しようとすると例外がスローされる")
        void shouldThrowExceptionWhenCreatingPageSizeWithNegativeValue() {
            // When & Then
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> PageSize.create(-1)
            );
            assertFalse(exception.getErrors().isEmpty());
            assertEquals("page_size", exception.getErrors().get(0).field());
            assertEquals("validation.pageSize.min", exception.getErrors().get(0).messageKey());
        }

        @Test
        @DisplayName("101以上でページサイズを作成しようとすると例外がスローされる")
        void shouldThrowExceptionWhenCreatingPageSizeExceedingMaxValue() {
            // When & Then
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> PageSize.create(101)
            );
            assertFalse(exception.getErrors().isEmpty());
            assertEquals("page_size", exception.getErrors().get(0).field());
            assertEquals("validation.pageSize.max", exception.getErrors().get(0).messageKey());
        }

        @Test
        @DisplayName("nullでページサイズを作成しようとすると例外がスローされる")
        void shouldThrowExceptionWhenCreatingPageSizeWithNull() {
            // When & Then
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> PageSize.create(null)
            );
            assertFalse(exception.getErrors().isEmpty());
            assertEquals("page_size", exception.getErrors().get(0).field());
        }

        @Test
        @DisplayName("validateメソッドがnullの場合にエラーを返す")
        void shouldReturnErrorWhenValidatingNull() {
            // When
            List<ValidationError> errors = PageSize.validate(null);

            // Then
            assertFalse(errors.isEmpty());
            assertEquals("page_size", errors.get(0).field());
        }

        @Test
        @DisplayName("validateメソッドが0の場合にエラーを返す")
        void shouldReturnErrorWhenValidatingZero() {
            // When
            List<ValidationError> errors = PageSize.validate(0);

            // Then
            assertFalse(errors.isEmpty());
            assertEquals("page_size", errors.get(0).field());
            assertEquals("validation.pageSize.min", errors.get(0).messageKey());
        }

        @Test
        @DisplayName("validateメソッドが負の値の場合にエラーを返す")
        void shouldReturnErrorWhenValidatingNegativeValue() {
            // When
            List<ValidationError> errors = PageSize.validate(-1);

            // Then
            assertFalse(errors.isEmpty());
            assertEquals("page_size", errors.get(0).field());
            assertEquals("validation.pageSize.min", errors.get(0).messageKey());
        }

        @Test
        @DisplayName("validateメソッドが101以上の場合にエラーを返す")
        void shouldReturnErrorWhenValidatingExceedingMaxValue() {
            // When
            List<ValidationError> errors = PageSize.validate(101);

            // Then
            assertFalse(errors.isEmpty());
            assertEquals("page_size", errors.get(0).field());
            assertEquals("validation.pageSize.max", errors.get(0).messageKey());
        }
    }
}
