package com.example.invoicepaymentapi.domain.model.auth;

import com.example.invoicepaymentapi.domain.exception.DomainValidationException;
import com.example.invoicepaymentapi.domain.exception.ValidationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * JWTアクセストークン値オブジェクト
 */
public record AccessToken(String value) {
    private static final Logger log = LoggerFactory.getLogger(AccessToken.class);

    /**
     * 新規作成時のファクトリメソッド
     * バリデーションを実施
     */
    public static AccessToken ofCreate(String value) {
        List<ValidationError> errors = new ArrayList<>();

        if (value == null || value.isEmpty()) {
            errors.add(ValidationError.required("accessToken"));
        } else {
            //TODO: JWTの検証ルールをできればここに持ちたい。
        }

        if (!errors.isEmpty()) {
            throw new DomainValidationException(errors);
        }

        return new AccessToken(value);
    }
}

