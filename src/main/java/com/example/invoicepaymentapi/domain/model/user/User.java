package com.example.invoicepaymentapi.domain.model.user;

import java.time.LocalDateTime;

/**
 * ユーザー集約ルート
 */
public record User(
        UserId id,
        CompanyName companyName,
        UserName name,
        Email email,
        HashedPassword password,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    /**
     * 新規ユーザーを作成するファクトリメソッド
     */
    public static User create(
            CompanyName companyName,
            UserName name,
            Email email,
            HashedPassword password
    ) {
        LocalDateTime now = LocalDateTime.now();
        return new User(
                null, // 新規作成時はIDは未設定
                companyName,
                name,
                email,
                password,
                now,
                now
        );
    }

    /**
     * 既存ユーザーを再構築するファクトリメソッド（リポジトリから取得時など）
     */
    public static User reconstruct(
            UserId id,
            CompanyName companyName,
            UserName name,
            Email email,
            HashedPassword password,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        return new User(
                id,
                companyName,
                name,
                email,
                password,
                createdAt,
                updatedAt
        );
    }
}
