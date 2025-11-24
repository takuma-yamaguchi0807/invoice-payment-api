package com.example.invoicepaymentapi.infrastructure.repository;

import com.example.invoicepaymentapi.domain.model.user.*;
import com.example.invoicepaymentapi.domain.repository.UserRepository;
import com.example.invoicepaymentapi.infrastructure.entity.UserEntity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import com.example.invoicepaymentapi.domain.model.user.UserId;

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
    public void save(User user) {
        UserEntity entity = toEntity(user);
        if (entity.getId() == null) {
            entityManager.persist(entity);
        } else {
            entityManager.merge(entity);
        }
        entityManager.flush();
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

    @Override
    public Optional<User> findById(UserId userId) {
        UserEntity entity = entityManager.find(UserEntity.class, userId.value());
        if (entity == null) {
            return Optional.empty();
        }
        return Optional.of(toDomain(entity));
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
        return User.reconstruct(
                UserId.reconstruct(entity.getId()),
                CompanyName.reconstruct(entity.getCompanyName()),
                UserName.reconstruct(entity.getName()),
                Email.reconstruct(entity.getEmail()),
                HashedPassword.reconstruct(entity.getPassword()),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}

