package com.example.invoicepaymentapi.domain.model.user;

import com.example.invoicepaymentapi.domain.exception.DomainValidationException;
import com.example.invoicepaymentapi.domain.exception.ValidationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 氏名値オブジェクト
 */
public record UserName(String value) {
    private static final Logger log = LoggerFactory.getLogger(UserName.class);
    private static final int MAX_LENGTH = 255;

    /**
     * 新規作成時のファクトリメソッド
     * バリデーションを実施
     */
    public static UserName ofCreate(String value) {
        List<ValidationError> errors = new ArrayList<>();

        if (value == null || value.isEmpty()) {
            errors.add(ValidationError.required("userName"));
        } else {
            if (value.length() > MAX_LENGTH) {
                errors.add(new ValidationError("userName", "validation.userName.maxLength"));
            }
        }

        if (!errors.isEmpty()) {
            throw new DomainValidationException(errors);
        }

        return new UserName(value);
    }

    /**
     * 既存データ取得時のファクトリメソッド
     * nullの場合はエラーログを出力して、valueがnullの値オブジェクトを返す（不正データの可能性）
     */
    public static UserName ofGet(String value) {
        if (value == null) {
            log.error("UserName cannot be null. Invalid data detected in database.");
        }
        return new UserName(value);
    }
}
