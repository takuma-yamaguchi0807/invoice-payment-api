package com.example.invoicepaymentapi.domain.model.user;

import com.example.invoicepaymentapi.domain.exception.DomainValidationException;
import com.example.invoicepaymentapi.domain.exception.ValidationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * パスワード値オブジェクト（ハッシュ化前）
 * 要件: 8文字以上、英大文字・小文字・数値・記号の4種のうち3種以上
 */
public record Password(String value) {
    private static final Logger log = LoggerFactory.getLogger(Password.class);
    private static final int MIN_LENGTH = 8;

    /**
     * 新規作成時のファクトリメソッド
     * バリデーションを実施
     */
    public static Password ofCreate(String value) {
        List<ValidationError> errors = validate(value);
        if (!errors.isEmpty()) {
            throw new DomainValidationException(errors);
        }
        return new Password(value);
    }

    /**
     * バリデーションを実行し、エラーのリストを返す
     * 例外を投げずにエラーを返すため、複数のフィールドのバリデーションを一括で実行できる
     *
     * @param value パスワード
     * @return バリデーションエラーのリスト（エラーがない場合は空のリスト）
     */
    public static List<ValidationError> validate(String value) {
        List<ValidationError> errors = new ArrayList<>();

        if (value == null || value.isEmpty()) {
            errors.add(ValidationError.required("password"));
        } else {
            if (value.length() < MIN_LENGTH) {
                errors.add(new ValidationError("password", "validation.password.length"));
            }
            if (!hasRequiredCharacterTypes(value)) {
                errors.add(new ValidationError("password", "validation.password.characterTypes"));
            }
        }

        return errors;
    }

    /**
     * 既存データ取得時のファクトリメソッド
     * nullの場合はエラーログを出力して、valueがnullの値オブジェクトを返す（不正データの可能性）
     */
    public static Password ofGet(String value) {
        if (value == null) {
            log.error("Password cannot be null. Invalid data detected in database.");
        }
        return new Password(value);
    }

    /**
     * パスワードが要件を満たしているかチェック
     * 英大文字・小文字・数値・記号の4種のうち3種以上を含む必要がある
     */
    private static boolean hasRequiredCharacterTypes(String password) {
        boolean hasUpper = Pattern.compile("[A-Z]").matcher(password).find();
        boolean hasLower = Pattern.compile("[a-z]").matcher(password).find();
        boolean hasDigit = Pattern.compile("[0-9]").matcher(password).find();
        boolean hasSpecial = Pattern.compile("[^A-Za-z0-9]").matcher(password).find();

        int typeCount = 0;
        if (hasUpper) typeCount++;
        if (hasLower) typeCount++;
        if (hasDigit) typeCount++;
        if (hasSpecial) typeCount++;

        return typeCount >= 3;
    }

    @Override
    public String toString() {
        return "Password{value='***'}";
    }
}
