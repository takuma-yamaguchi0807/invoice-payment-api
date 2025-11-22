package com.example.invoicepaymentapi.domain.model.user;

import com.example.invoicepaymentapi.domain.exception.DomainValidationException;
import com.example.invoicepaymentapi.domain.exception.ValidationError;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 企業名値オブジェクト
 */
public record CompanyName(String value) {
    private static final int MAX_LENGTH = 255;

    /**
     * 新規作成時のファクトリメソッド
     * バリデーションを実施
     */
    public static CompanyName create(String value) {
        List<ValidationError> errors = validate(value);
        if (!errors.isEmpty()) {
            throw new DomainValidationException(errors);
        }
        return new CompanyName(value);
    }

    /**
     * バリデーションを実行し、エラーのリストを返す
     * 例外を投げずにエラーを返すため、複数のフィールドのバリデーションを一括で実行できる
     *
     * @param value 企業名
     * @return バリデーションエラーのリスト（エラーがない場合は空のリスト）
     */
    public static List<ValidationError> validate(String value) {
        List<ValidationError> errors = new ArrayList<>();

        if (StringUtils.isEmpty(value)) {
            errors.add(ValidationError.required("companyName"));
        } else {
            if (value.length() > MAX_LENGTH) {
                errors.add(new ValidationError("companyName", "validation.maxLength", new Object[]{MAX_LENGTH}));
            }
        }

        return errors;
    }

    /**
     * 既存データ取得時のファクトリメソッド
     * テーブルがNOT NULL制約のため、nullが来ることはない
     *
     * @param value 企業名
     * @throws IllegalArgumentException valueがnullの場合
     */
    public static CompanyName reconstruct(String value) {
        if (value == null) {
            throw new IllegalArgumentException("CompanyName cannot be null");
        }
        return new CompanyName(value);
    }
}
