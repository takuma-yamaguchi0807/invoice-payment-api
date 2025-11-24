package com.example.invoicepaymentapi.domain.model.user;

import com.example.invoicepaymentapi.domain.exception.DomainValidationException;
import com.example.invoicepaymentapi.domain.exception.ValidationError;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 企業名値オブジェクトの単体テスト
 */
class CompanyNameTest {

    @Nested
    @DisplayName("正常系")
    class NormalCase {
        @Test
        @DisplayName("有効な企業名で値オブジェクトを作成できる")
        void shouldCreateCompanyNameWithValidValue() {
            // Given
            String companyName = "株式会社サンプル";

            // When
            CompanyName companyNameVo = CompanyName.create(companyName);

            // Then
            assertNotNull(companyNameVo);
            assertEquals(companyName, companyNameVo.value());
        }

        @Test
        @DisplayName("最大長（255文字）の企業名で値オブジェクトを作成できる")
        void shouldCreateCompanyNameWithMaxLength() {
            // Given
            String companyName = "a".repeat(255);

            // When
            CompanyName companyNameVo = CompanyName.create(companyName);

            // Then
            assertNotNull(companyNameVo);
            assertEquals(companyName, companyNameVo.value());
        }

        @Test
        @DisplayName("validateメソッドが有効な企業名の場合にエラーを返さない")
        void shouldNotReturnErrorWhenValidatingValidCompanyName() {
            // Given
            String companyName = "株式会社サンプル";

            // When
            List<ValidationError> errors = CompanyName.validate(companyName);

            // Then
            assertTrue(errors.isEmpty());
        }

        @Test
        @DisplayName("reconstructメソッドで有効な企業名で値オブジェクトを作成できる")
        void shouldReconstructCompanyNameWithValidValue() {
            // Given
            String companyName = "株式会社サンプル";

            // When
            CompanyName companyNameVo = CompanyName.reconstruct(companyName);

            // Then
            assertNotNull(companyNameVo);
            assertEquals(companyName, companyNameVo.value());
        }
    }

    @Nested
    @DisplayName("異常系")
    class AbnormalCase {
        @Test
        @DisplayName("nullで企業名を作成しようとすると例外がスローされる")
        void shouldThrowExceptionWhenCreatingCompanyNameWithNull() {
            // When & Then
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> CompanyName.create(null)
            );
            assertFalse(exception.getErrors().isEmpty());
            assertEquals("companyName", exception.getErrors().get(0).field());
        }

        @Test
        @DisplayName("空文字列で企業名を作成しようとすると例外がスローされる")
        void shouldThrowExceptionWhenCreatingCompanyNameWithEmptyString() {
            // When & Then
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> CompanyName.create("")
            );
            assertFalse(exception.getErrors().isEmpty());
            assertEquals("companyName", exception.getErrors().get(0).field());
        }

        @Test
        @DisplayName("255文字を超える企業名で作成しようとすると例外がスローされる")
        void shouldThrowExceptionWhenCreatingCompanyNameExceedingMaxLength() {
            // Given
            String companyName = "a".repeat(256);

            // When & Then
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> CompanyName.create(companyName)
            );
            assertFalse(exception.getErrors().isEmpty());
            assertEquals("companyName", exception.getErrors().get(0).field());
            assertEquals("validation.maxLength", exception.getErrors().get(0).messageKey());
        }

        @Test
        @DisplayName("validateメソッドがnullの場合にエラーを返す")
        void shouldReturnErrorWhenValidatingNull() {
            // When
            List<ValidationError> errors = CompanyName.validate(null);

            // Then
            assertFalse(errors.isEmpty());
            assertEquals("companyName", errors.get(0).field());
        }

        @Test
        @DisplayName("validateメソッドが空文字列の場合にエラーを返す")
        void shouldReturnErrorWhenValidatingEmptyString() {
            // When
            List<ValidationError> errors = CompanyName.validate("");

            // Then
            assertFalse(errors.isEmpty());
            assertEquals("companyName", errors.get(0).field());
        }

        @Test
        @DisplayName("validateメソッドが255文字を超える場合にエラーを返す")
        void shouldReturnErrorWhenValidatingExceedingMaxLength() {
            // Given
            String companyName = "a".repeat(256);

            // When
            List<ValidationError> errors = CompanyName.validate(companyName);

            // Then
            assertFalse(errors.isEmpty());
            assertEquals("companyName", errors.get(0).field());
            assertEquals("validation.maxLength", errors.get(0).messageKey());
        }

        @Test
        @DisplayName("reconstructメソッドでnullを渡すとIllegalArgumentExceptionがスローされる")
        void shouldThrowIllegalArgumentExceptionWhenReconstructingWithNull() {
            // When & Then
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> CompanyName.reconstruct(null)
            );
            assertEquals("CompanyName cannot be null", exception.getMessage());
        }
    }
}
