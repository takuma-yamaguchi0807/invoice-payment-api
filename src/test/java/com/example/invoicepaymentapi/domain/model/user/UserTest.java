package com.example.invoicepaymentapi.domain.model.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ユーザーエンティティの単体テスト
 */
class UserTest {

    @Nested
    @DisplayName("正常系")
    class NormalCase {
        @Test
        @DisplayName("有効な値でユーザーを作成できる")
        void shouldCreateUserWithValidValues() {
            // Given
            CompanyName companyName = CompanyName.create("株式会社サンプル");
            UserName name = UserName.create("山田太郎");
            Email email = Email.create("yamada@example.com");
            HashedPassword password = HashedPassword.reconstruct("$argon2id$v=19$m=65536,t=3,p=4$hash");

            // When
            User user = User.create(companyName, name, email, password);

            // Then
            assertNotNull(user);
            assertNull(user.id()); // 新規作成時はIDはnull
            assertEquals(companyName, user.companyName());
            assertEquals(name, user.name());
            assertEquals(email, user.email());
            assertEquals(password, user.password());
            assertNotNull(user.createdAt());
            assertNotNull(user.updatedAt());
        }

        @Test
        @DisplayName("ユーザー作成時に作成日時と更新日時が設定される")
        void shouldSetCreatedAtAndUpdatedAt() {
            // Given
            CompanyName companyName = CompanyName.create("株式会社サンプル");
            UserName name = UserName.create("山田太郎");
            Email email = Email.create("yamada@example.com");
            HashedPassword password = HashedPassword.reconstruct("$argon2id$v=19$m=65536,t=3,p=4$hash");
            LocalDateTime beforeCreation = LocalDateTime.now();

            // When
            User user = User.create(companyName, name, email, password);
            LocalDateTime afterCreation = LocalDateTime.now();

            // Then
            assertNotNull(user.createdAt());
            assertNotNull(user.updatedAt());
            assertTrue(user.createdAt().isAfter(beforeCreation) || user.createdAt().isEqual(beforeCreation));
            assertTrue(user.createdAt().isBefore(afterCreation) || user.createdAt().isEqual(afterCreation));
            assertEquals(user.createdAt(), user.updatedAt());
        }

        @Test
        @DisplayName("reconstructメソッドで既存のユーザーを再構築できる")
        void shouldReconstructUser() {
            // Given
            UserId id = UserId.create(1);
            CompanyName companyName = CompanyName.create("株式会社サンプル");
            UserName name = UserName.create("山田太郎");
            Email email = Email.create("yamada@example.com");
            HashedPassword password = HashedPassword.reconstruct("$argon2id$v=19$m=65536,t=3,p=4$hash");
            LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
            LocalDateTime updatedAt = LocalDateTime.now();

            // When
            User user = User.reconstruct(id, companyName, name, email, password, createdAt, updatedAt);

            // Then
            assertNotNull(user);
            assertEquals(id, user.id());
            assertEquals(companyName, user.companyName());
            assertEquals(name, user.name());
            assertEquals(email, user.email());
            assertEquals(password, user.password());
            assertEquals(createdAt, user.createdAt());
            assertEquals(updatedAt, user.updatedAt());
        }

        @Test
        @DisplayName("同じハッシュ化パスワードでverifyPasswordがtrueを返す")
        void shouldReturnTrueWhenVerifyingSameHashedPassword() {
            // Given
            CompanyName companyName = CompanyName.create("株式会社サンプル");
            UserName name = UserName.create("山田太郎");
            Email email = Email.create("yamada@example.com");
            HashedPassword password = HashedPassword.reconstruct("$argon2id$v=19$m=65536,t=3,p=4$hash");
            User user = User.create(companyName, name, email, password);

            // When
            boolean result = user.verifyPassword(password);

            // Then
            assertTrue(result);
        }
    }

    @Nested
    @DisplayName("異常系")
    class AbnormalCase {
        @Test
        @DisplayName("異なるハッシュ化パスワードでverifyPasswordがfalseを返す")
        void shouldReturnFalseWhenVerifyingDifferentHashedPassword() {
            // Given
            CompanyName companyName = CompanyName.create("株式会社サンプル");
            UserName name = UserName.create("山田太郎");
            Email email = Email.create("yamada@example.com");
            HashedPassword password = HashedPassword.reconstruct("$argon2id$v=19$m=65536,t=3,p=4$hash");
            HashedPassword differentPassword = HashedPassword.reconstruct("$argon2id$v=19$m=65536,t=3,p=4$different");
            User user = User.create(companyName, name, email, password);

            // When
            boolean result = user.verifyPassword(differentPassword);

            // Then
            assertFalse(result);
        }
    }
}
