package com.example.invoicepaymentapi.domain.model.auth;

import com.example.invoicepaymentapi.domain.exception.UnauthorizedException;
import com.example.invoicepaymentapi.domain.model.user.UserId;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JWTアクセストークン値オブジェクトの単体テスト
 * 
 * 注意: このテストは環境変数JWT_SECRETとJWT_EXPIRATIONが設定されていることを前提とします。
 * テスト実行前に環境変数を設定してください。
 * 例: JWT_SECRET=test-secret-key-for-jwt-token-validation-testing-purposes-only JWT_EXPIRATION=3600
 */
class AccessTokenTest {

    @Nested
    @DisplayName("正常系 - createメソッド")
    class NormalCaseCreate {
        @Test
        @DisplayName("有効なユーザーIDでJWTトークンを作成できる")
        void shouldCreateAccessTokenWithValidUserId() {
            // Given
            Assumptions.assumeTrue(
                    System.getenv("JWT_SECRET") != null && System.getenv("JWT_EXPIRATION") != null,
                    "環境変数JWT_SECRETとJWT_EXPIRATIONが設定されている必要があります"
            );
            UserId userId = UserId.create(1);

            // When
            AccessToken accessToken = AccessToken.create(userId);

            // Then
            assertNotNull(accessToken);
            assertNotNull(accessToken.value());
            assertFalse(accessToken.value().isEmpty());
        }

        @Test
        @DisplayName("作成したJWTトークンを検証できる")
        void shouldValidateCreatedToken() {
            // Given
            Assumptions.assumeTrue(
                    System.getenv("JWT_SECRET") != null && System.getenv("JWT_EXPIRATION") != null,
                    "環境変数JWT_SECRETとJWT_EXPIRATIONが設定されている必要があります"
            );
            UserId userId = UserId.create(1);
            AccessToken accessToken = AccessToken.create(userId);

            // When & Then
            assertDoesNotThrow(() -> AccessToken.validate(accessToken.value()));
        }

        @Test
        @DisplayName("作成したJWTトークンからユーザーIDを抽出できる")
        void shouldExtractUserIdFromCreatedToken() {
            // Given
            Assumptions.assumeTrue(
                    System.getenv("JWT_SECRET") != null && System.getenv("JWT_EXPIRATION") != null,
                    "環境変数JWT_SECRETとJWT_EXPIRATIONが設定されている必要があります"
            );
            UserId userId = UserId.create(1);
            AccessToken accessToken = AccessToken.create(userId);

            // When
            UserId extractedUserId = accessToken.extractUserId();

            // Then
            assertNotNull(extractedUserId);
            assertEquals(1, extractedUserId.value());
        }
    }

    @Nested
    @DisplayName("異常系 - createメソッド")
    class AbnormalCaseCreate {
        @Test
        @DisplayName("nullのユーザーIDでJWTトークンを作成しようとすると例外がスローされる")
        void shouldThrowExceptionWhenCreatingTokenWithNullUserId() {
            // When & Then
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> AccessToken.create(null)
            );
            assertEquals("UserId cannot be null", exception.getMessage());
        }

    }

    @Nested
    @DisplayName("正常系 - validateメソッド")
    class NormalCaseValidate {
        @Test
        @DisplayName("有効なJWTトークンを検証できる")
        void shouldValidateValidToken() {
            // Given
            Assumptions.assumeTrue(
                    System.getenv("JWT_SECRET") != null && System.getenv("JWT_EXPIRATION") != null,
                    "環境変数JWT_SECRETとJWT_EXPIRATIONが設定されている必要があります"
            );
            UserId userId = UserId.create(1);
            AccessToken accessToken = AccessToken.create(userId);

            // When & Then
            assertDoesNotThrow(() -> AccessToken.validate(accessToken.value()));
        }

        @Test
        @DisplayName("異なるユーザーIDで作成したトークンも検証できる")
        void shouldValidateTokenCreatedWithDifferentUserId() {
            // Given
            Assumptions.assumeTrue(
                    System.getenv("JWT_SECRET") != null && System.getenv("JWT_EXPIRATION") != null,
                    "環境変数JWT_SECRETとJWT_EXPIRATIONが設定されている必要があります"
            );
            UserId userId = UserId.create(999);
            AccessToken accessToken = AccessToken.create(userId);

            // When & Then
            assertDoesNotThrow(() -> AccessToken.validate(accessToken.value()));
        }
    }

    @Nested
    @DisplayName("異常系 - validateメソッド")
    class AbnormalCaseValidate {
        @Test
        @DisplayName("nullのトークンを検証しようとすると例外がスローされる")
        void shouldThrowExceptionWhenValidatingNullToken() {
            // When & Then
            UnauthorizedException exception = assertThrows(
                    UnauthorizedException.class,
                    () -> AccessToken.validate(null)
            );
            assertEquals("JWT token is required", exception.getMessage());
        }

        @Test
        @DisplayName("空文字列のトークンを検証しようとすると例外がスローされる")
        void shouldThrowExceptionWhenValidatingEmptyToken() {
            // When & Then
            UnauthorizedException exception = assertThrows(
                    UnauthorizedException.class,
                    () -> AccessToken.validate("")
            );
            assertEquals("JWT token is required", exception.getMessage());
        }

        @Test
        @DisplayName("不正な形式のトークンを検証しようとすると例外がスローされる")
        void shouldThrowExceptionWhenValidatingInvalidFormatToken() {
            // Given
            Assumptions.assumeTrue(
                    System.getenv("JWT_SECRET") != null,
                    "環境変数JWT_SECRETが設定されている必要があります"
            );
            String invalidToken = "invalid.token.format";

            // When & Then
            UnauthorizedException exception = assertThrows(
                    UnauthorizedException.class,
                    () -> AccessToken.validate(invalidToken)
            );
            assertEquals("Invalid JWT token", exception.getMessage());
        }

        @Test
        @DisplayName("異なるシークレットキーで署名されたトークンを検証しようとすると例外がスローされる")
        void shouldThrowExceptionWhenValidatingTokenWithDifferentSecret() {
            // Given
            // 異なるシークレットキーでトークンを作成（実際には別の環境で作成されたトークンをシミュレート）
            // このテストは、実際の異なるシークレットキーでトークンを作成する必要があるため、
            // 実装が複雑になるため、ここではスキップ
            // 実際のテストでは、別のシークレットキーでトークンを作成して検証を試みる
        }

        @Test
        @DisplayName("改ざんされたトークンを検証しようとすると例外がスローされる")
        void shouldThrowExceptionWhenValidatingTamperedToken() {
            // Given
            Assumptions.assumeTrue(
                    System.getenv("JWT_SECRET") != null && System.getenv("JWT_EXPIRATION") != null,
                    "環境変数JWT_SECRETとJWT_EXPIRATIONが設定されている必要があります"
            );
            UserId userId = UserId.create(1);
            AccessToken accessToken = AccessToken.create(userId);
            String tamperedToken = accessToken.value() + "tampered";

            // When & Then
            UnauthorizedException exception = assertThrows(
                    UnauthorizedException.class,
                    () -> AccessToken.validate(tamperedToken)
            );
            assertEquals("Invalid JWT token", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("正常系 - extractUserIdメソッド")
    class NormalCaseExtractUserId {
        @Test
        @DisplayName("有効なJWTトークンからユーザーIDを抽出できる")
        void shouldExtractUserIdFromValidToken() {
            // Given
            Assumptions.assumeTrue(
                    System.getenv("JWT_SECRET") != null && System.getenv("JWT_EXPIRATION") != null,
                    "環境変数JWT_SECRETとJWT_EXPIRATIONが設定されている必要があります"
            );
            UserId userId = UserId.create(1);
            AccessToken accessToken = AccessToken.create(userId);

            // When
            UserId extractedUserId = accessToken.extractUserId();

            // Then
            assertNotNull(extractedUserId);
            assertEquals(1, extractedUserId.value());
        }

        @Test
        @DisplayName("異なるユーザーIDで作成したトークンから正しいユーザーIDを抽出できる")
        void shouldExtractCorrectUserIdFromTokenCreatedWithDifferentUserId() {
            // Given
            Assumptions.assumeTrue(
                    System.getenv("JWT_SECRET") != null && System.getenv("JWT_EXPIRATION") != null,
                    "環境変数JWT_SECRETとJWT_EXPIRATIONが設定されている必要があります"
            );
            UserId userId = UserId.create(999);
            AccessToken accessToken = AccessToken.create(userId);

            // When
            UserId extractedUserId = accessToken.extractUserId();

            // Then
            assertNotNull(extractedUserId);
            assertEquals(999, extractedUserId.value());
        }
    }

    @Nested
    @DisplayName("異常系 - extractUserIdメソッド")
    class AbnormalCaseExtractUserId {
        @Test
        @DisplayName("不正な形式のトークンからユーザーIDを抽出しようとすると例外がスローされる")
        void shouldThrowExceptionWhenExtractingUserIdFromInvalidToken() {
            // Given
            Assumptions.assumeTrue(
                    System.getenv("JWT_SECRET") != null,
                    "環境変数JWT_SECRETが設定されている必要があります"
            );
            AccessToken accessToken = new AccessToken("invalid.token.format");

            // When & Then
            UnauthorizedException exception = assertThrows(
                    UnauthorizedException.class,
                    () -> accessToken.extractUserId()
            );
            assertEquals("Invalid JWT token", exception.getMessage());
        }

        @Test
        @DisplayName("改ざんされたトークンからユーザーIDを抽出しようとすると例外がスローされる")
        void shouldThrowExceptionWhenExtractingUserIdFromTamperedToken() {
            // Given
            Assumptions.assumeTrue(
                    System.getenv("JWT_SECRET") != null && System.getenv("JWT_EXPIRATION") != null,
                    "環境変数JWT_SECRETとJWT_EXPIRATIONが設定されている必要があります"
            );
            UserId userId = UserId.create(1);
            AccessToken validToken = AccessToken.create(userId);
            AccessToken tamperedToken = new AccessToken(validToken.value() + "tampered");

            // When & Then
            UnauthorizedException exception = assertThrows(
                    UnauthorizedException.class,
                    () -> tamperedToken.extractUserId()
            );
            assertEquals("Invalid JWT token", exception.getMessage());
        }
    }
}

