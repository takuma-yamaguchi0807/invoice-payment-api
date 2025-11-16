package com.example.invoicepaymentapi.infrastructure.persistence.repository;

import com.example.invoicepaymentapi.domain.model.user.*;
import com.example.invoicepaymentapi.domain.repository.UserRepository;
import com.example.invoicepaymentapi.infrastructure.persistence.entity.UserEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * ユーザーリポジトリ実装
 * ドメイン層のUserRepositoryインターフェースを実装
 */
@Repository
public class UserRepositoryImpl implements UserRepository {
    private final EntityManager entityManager;

    public UserRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public User save(User user) {
        UserEntity entity = toEntity(user);
        if (entity.getId() == null) {
            entityManager.persist(entity);
        } else {
            entity = entityManager.merge(entity);
        }
        entityManager.flush();
        return toDomain(entity);
    }

    @Override
    public Optional<User> findByEmail(Email email) {
        TypedQuery<UserEntity> query = entityManager.createQuery(
                "SELECT u FROM UserEntity u WHERE u.email = :email",
                UserEntity.class
        );
        query.setParameter("email", email.value());
        return query.getResultStream()
                .findFirst()
                .map(this::toDomain);
    }

    /**
     * ドメインモデルからJPAエンティティに変換
     */
    private UserEntity toEntity(User user) {
        UserEntity entity = new UserEntity();
        if (user.id() != null) {
            entity.setId(user.id().value());
        }
        entity.setCompanyName(user.companyName().value());
        entity.setName(user.name().value());
        entity.setEmail(user.email().value());
        entity.setPassword(user.password().value());
        entity.setCreatedAt(user.createdAt());
        entity.setUpdatedAt(user.updatedAt());
        return entity;
    }

    /**
     * JPAエンティティからドメインモデルに変換
     */
    private User toDomain(UserEntity entity) {
        return User.ofGet(
                UserId.ofGet(entity.getId()),
                CompanyName.ofGet(entity.getCompanyName()),
                UserName.ofGet(entity.getName()),
                Email.ofGet(entity.getEmail()),
                HashedPassword.ofGet(entity.getPassword()),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}

