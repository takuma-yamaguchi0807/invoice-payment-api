package com.example.invoicepaymentapi.infrastructure.repository;

import com.example.invoicepaymentapi.domain.model.user.*;
import com.example.invoicepaymentapi.infrastructure.entity.UserEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * ユーザーリポジトリ実装の単体テスト
 */
@ExtendWith(MockitoExtension.class)
class UserRepositoryImplTest {
    @Mock
    private EntityManager entityManager;

    @Mock
    private TypedQuery<UserEntity> userQuery;

    @InjectMocks
    private UserRepositoryImpl userRepository;

    private User testUser;
    private UserEntity testEntity;

    @BeforeEach
    void setUp() {
        CompanyName companyName = CompanyName.reconstruct("株式会社サンプル");
        UserName name = UserName.reconstruct("山田太郎");
        Email email = Email.reconstruct("yamada@example.com");
        HashedPassword password = HashedPassword.reconstruct("$argon2id$v=19$m=65536,t=3,p=4$hash");
        LocalDateTime now = LocalDateTime.now();

        testUser = User.reconstruct(
                null,
                companyName,
                name,
                email,
                password,
                now,
                now
        );

        testEntity = new UserEntity();
        testEntity.setId(1);
        testEntity.setCompanyName("株式会社サンプル");
        testEntity.setName("山田太郎");
        testEntity.setEmail("yamada@example.com");
        testEntity.setPassword("$argon2id$v=19$m=65536,t=3,p=4$hash");
        testEntity.setCreatedAt(now);
        testEntity.setUpdatedAt(now);
    }

    @Nested
    @DisplayName("saveメソッド")
    class SaveTest {
        @Test
        @DisplayName("新規保存が成功する")
        void shouldSaveNewUser() {
            // Given
            User user = testUser;

            // When
            userRepository.save(user);

            // Then
            ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
            verify(entityManager).persist(captor.capture());
            verify(entityManager).flush();
            verify(entityManager, never()).merge(any(UserEntity.class));
            UserEntity entity = captor.getValue();
            assertThat(entity.getEmail()).isEqualTo("yamada@example.com");
        }

        @Test
        @DisplayName("更新が成功する")
        void shouldUpdateExistingUser() {
            // Given
            User user = User.reconstruct(
                    UserId.reconstruct(1),
                    CompanyName.reconstruct("株式会社サンプル"),
                    UserName.reconstruct("山田太郎"),
                    Email.reconstruct("yamada@example.com"),
                    HashedPassword.reconstruct("$argon2id$v=19$m=65536,t=3,p=4$hash"),
                    LocalDateTime.now(),
                    LocalDateTime.now()
            );
            when(entityManager.merge(any(UserEntity.class))).thenReturn(testEntity);

            // When
            userRepository.save(user);

            // Then
            verify(entityManager).merge(any(UserEntity.class));
            verify(entityManager).flush();
            verify(entityManager, never()).persist(any(UserEntity.class));
        }
    }

    @Nested
    @DisplayName("findByEmailメソッド")
    class FindByEmailTest {
        @Test
        @DisplayName("メールアドレスで検索が成功する")
        void shouldFindUserByEmail() {
            // Given
            Email email = Email.reconstruct("yamada@example.com");
            when(entityManager.createQuery(anyString(), eq(UserEntity.class))).thenReturn(userQuery);
            when(userQuery.setParameter("email", "yamada@example.com")).thenReturn(userQuery);
            when(userQuery.getResultStream()).thenReturn(Stream.of(testEntity));

            // When
            Optional<User> result = userRepository.findByEmail(email);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().email().value()).isEqualTo("yamada@example.com");
            verify(userQuery).setParameter("email", "yamada@example.com");
        }

        @Test
        @DisplayName("メールアドレスが存在しない場合、空のOptionalを返す")
        void shouldReturnEmptyWhenEmailNotFound() {
            // Given
            Email email = Email.reconstruct("nonexistent@example.com");
            when(entityManager.createQuery(anyString(), eq(UserEntity.class))).thenReturn(userQuery);
            when(userQuery.setParameter("email", "nonexistent@example.com")).thenReturn(userQuery);
            when(userQuery.getResultStream()).thenReturn(Stream.empty());

            // When
            Optional<User> result = userRepository.findByEmail(email);

            // Then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findByIdメソッド")
    class FindByIdTest {
        @Test
        @DisplayName("IDで検索が成功する")
        void shouldFindUserById() {
            // Given
            UserId userId = UserId.reconstruct(1);
            when(entityManager.find(UserEntity.class, 1)).thenReturn(testEntity);

            // When
            Optional<User> result = userRepository.findById(userId);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().id().value()).isEqualTo(1);
            assertThat(result.get().email().value()).isEqualTo("yamada@example.com");
        }

        @Test
        @DisplayName("IDが存在しない場合、空のOptionalを返す")
        void shouldReturnEmptyWhenIdNotFound() {
            // Given
            UserId userId = UserId.reconstruct(999);
            when(entityManager.find(UserEntity.class, 999)).thenReturn(null);

            // When
            Optional<User> result = userRepository.findById(userId);

            // Then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("toEntityメソッド")
    class ToEntityTest {
        @Test
        @DisplayName("ドメインモデルからエンティティへの変換が成功する")
        void shouldConvertDomainToEntity() {
            // Given
            User user = testUser;

            // When
            userRepository.save(user);

            // Then
            ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
            verify(entityManager).persist(captor.capture());
            UserEntity entity = captor.getValue();
            assertThat(entity.getCompanyName()).isEqualTo("株式会社サンプル");
            assertThat(entity.getName()).isEqualTo("山田太郎");
            assertThat(entity.getEmail()).isEqualTo("yamada@example.com");
        }
    }

    @Nested
    @DisplayName("toDomainメソッド")
    class ToDomainTest {
        @Test
        @DisplayName("エンティティからドメインモデルへの変換が成功する")
        void shouldConvertEntityToDomain() {
            // Given
            when(entityManager.find(UserEntity.class, 1)).thenReturn(testEntity);

            // When
            Optional<User> result = userRepository.findById(UserId.reconstruct(1));

            // Then
            assertThat(result).isPresent();
            User user = result.get();
            assertThat(user.id().value()).isEqualTo(1);
            assertThat(user.companyName().value()).isEqualTo("株式会社サンプル");
            assertThat(user.name().value()).isEqualTo("山田太郎");
            assertThat(user.email().value()).isEqualTo("yamada@example.com");
        }
    }
}

