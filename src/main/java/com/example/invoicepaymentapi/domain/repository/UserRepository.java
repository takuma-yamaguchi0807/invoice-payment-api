package com.example.invoicepaymentapi.domain.repository;

import com.example.invoicepaymentapi.domain.model.user.Email;
import com.example.invoicepaymentapi.domain.model.user.User;
import com.example.invoicepaymentapi.domain.model.user.UserId;

import java.util.Optional;

/**
 * ユーザーリポジトリインターフェース
 * ドメイン層の集約ルートを扱う
 */
public interface UserRepository {
    /**
     * ユーザーを保存
     *
     * @param user 保存するユーザー
     */
    void save(User user);

    /**
     * メールアドレスでユーザーを検索
     *
     * @param email 検索するメールアドレス
     * @return 見つかったユーザー（存在しない場合は空）
     */
    Optional<User> findByEmail(Email email);

    /**
     * ユーザーIDでユーザーを検索
     *
     * @param userId 検索するユーザーID
     * @return 見つかったユーザー（存在しない場合は空）
     */
    Optional<User> findById(UserId userId);
}

