package com.example.invoicepaymentapi.domain.repository;

import com.example.invoicepaymentapi.domain.model.user.Email;
import com.example.invoicepaymentapi.domain.model.user.User;

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
     * @return 保存されたユーザー（IDが設定された状態）
     */
    User save(User user);

    /**
     * メールアドレスでユーザーを検索
     *
     * @param email 検索するメールアドレス
     * @return 見つかったユーザー（存在しない場合は空）
     */
    Optional<User> findByEmail(Email email);
}

