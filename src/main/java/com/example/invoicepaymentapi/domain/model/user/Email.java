package com.example.invoicepaymentapi.domain.model.user;

import com.example.invoicepaymentapi.domain.exception.DomainValidationException;
import com.example.invoicepaymentapi.domain.exception.ValidationError;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * メールアドレス値オブジェクト
 * 
 * RFC 5321, RFC 5322に準拠したバリデーションを実装
 * - 最大長: 254文字（RFC 5321準拠）
 * - ローカル部: 連続するドットを禁止、先頭・末尾のドットを禁止（RFC 5322準拠）
 * - ドメイン部: 適切なドメイン形式
 * 
 * 正規表現の参考:
 * - RFC 5321: https://tools.ietf.org/html/rfc5321 (メールアドレスの最大長: 254文字)
 * - RFC 5322: https://tools.ietf.org/html/rfc5322 (メールアドレスの構文規則)
 * - ローカル部の構文: dot-atom形式（連続ドット禁止、先頭・末尾ドット禁止）
 */
public record Email(String value) {
    /**
     * RFC 5321で定められたメールアドレスの最大長（@を含む）
     * ローカル部: 最大64文字、ドメイン部: 最大255文字、全体: 最大254文字
     */
    private static final int MAX_LENGTH = 254;
    
    /**
     * RFC 5322準拠のメールアドレス正規表現
     * 
     * ローカル部: [A-Za-z0-9]([A-Za-z0-9+_.-]*[A-Za-z0-9])?
     *   - 先頭と末尾は英数字
     *   - 間にドットが連続しない（[A-Za-z0-9+_.-]*でドットを含むが、連続は自然に禁止される）
     * 
     * ドメイン部: [A-Za-z0-9]([A-Za-z0-9-]*[A-Za-z0-9])?(\.[A-Za-z0-9]([A-Za-z0-9-]*[A-Za-z0-9])?)*\.[A-Za-z]{2,}
     *   - 各ラベルは英数字で始まり英数字で終わる
     *   - ハイフンを含むことができる
     *   - 最後にトップレベルドメイン（2文字以上）
     * 
     */
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            // ローカル部: 英数字で始まり、英数字で終わる。間にドットが連続しない
            "^[A-Za-z0-9]([A-Za-z0-9+_.-]*[A-Za-z0-9])?" +
            "@" +
            // ドメイン部: 各ラベルは英数字で始まり英数字で終わる、ハイフンを含むことができる
            "[A-Za-z0-9]([A-Za-z0-9-]*[A-Za-z0-9])?" +
            "(\\.[A-Za-z0-9]([A-Za-z0-9-]*[A-Za-z0-9])?)*" +
            "\\.[A-Za-z]{2,}$"
    );

    /**
     * 新規作成時のファクトリメソッド
     * バリデーションを実施
     */
    public static Email create(String value) {
        List<ValidationError> errors = validate(value);
        if (!errors.isEmpty()) {
            throw new DomainValidationException(errors);
        }
        return new Email(value);
    }

    /**
     * バリデーションを実行し、エラーのリストを返す
     * 例外を投げずにエラーを返すため、複数のフィールドのバリデーションを一括で実行できる
     *
     * @param value メールアドレス
     * @return バリデーションエラーのリスト（エラーがない場合は空のリスト）
     */
    public static List<ValidationError> validate(String value) {
        List<ValidationError> errors = new ArrayList<>();

        if (StringUtils.isEmpty(value)) {
            errors.add(ValidationError.required("email"));
        } else {
            if (value.length() > MAX_LENGTH) {
                errors.add(new ValidationError("email", "validation.maxLength", new Object[]{MAX_LENGTH}));
            }
            // 連続するドットをチェック（RFC 5322準拠）
            if (value.contains("..")) {
                errors.add(new ValidationError("email", "validation.email.format"));
            } else if (!EMAIL_PATTERN.matcher(value).matches()) {
                errors.add(new ValidationError("email", "validation.email.format"));
            }
        }

        return errors;
    }

    /**
     * 既存データ取得時のファクトリメソッド
     * テーブルがNOT NULL制約のため、nullが来ることはない
     *
     * @param value メールアドレス
     * @throws IllegalArgumentException valueがnullの場合
     */
    public static Email reconstruct(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Email cannot be null");
        }
        return new Email(value);
    }
}
