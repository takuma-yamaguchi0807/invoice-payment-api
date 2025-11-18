package com.example.invoicepaymentapi.domain.model.user;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ハッシュ化済みパスワード値オブジェクト
 * Argon2でハッシュ化されたパスワードを保持
 */
public record HashedPassword(String value) {
    private static final Logger log = LoggerFactory.getLogger(HashedPassword.class);
    private static final Argon2 ARGON2 = Argon2Factory.create();

    /**
     * パスワードからハッシュ化済みパスワードを生成
     * Argon2を使用してハッシュ化
     *
     * @param password ハッシュ化前のパスワード
     * @return ハッシュ化済みパスワード
     * @note Password.createで既に値チェック（バリデーション）が行われているため、
     *       このメソッドでは追加の検証は不要
     */
    public static HashedPassword create(Password password) {
        String hash = ARGON2.hash(2, 65536, 1, password.value().toCharArray());
        return new HashedPassword(hash);
    }

    /**
     * 既存データ取得時のファクトリメソッド
     * nullの場合はエラーログを出力して、valueがnullの値オブジェクトを返す（不正データの可能性）
     */
    public static HashedPassword reconstruct(String value) {
        if (value == null) {
            log.error("HashedPassword cannot be null. Invalid data detected in database.");
        }
        return new HashedPassword(value);
    }

    /**
     * 生のパスワードがこのハッシュ化済みパスワードと一致するか検証
     * ログイン時のパスワード検証で使用
     *
     * @param rawPassword 検証する生のパスワード
     * @return パスワードが一致する場合true、一致しない場合false
     */
    public boolean verify(Password rawPassword) {
        if (this.value == null) {
            log.warn("HashedPassword value is null. Cannot verify password.");
            return false;
        }
        if (rawPassword == null || rawPassword.value() == null) {
            return false;
        }
        return ARGON2.verify(this.value, rawPassword.value().toCharArray());
    }

    @Override
    public String toString() {
        return "HashedPassword{value='***'}";
    }
}
