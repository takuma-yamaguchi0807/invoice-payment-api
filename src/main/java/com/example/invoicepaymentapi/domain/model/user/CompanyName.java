package com.example.invoicepaymentapi.domain.model.user;

import com.example.invoicepaymentapi.domain.exception.DomainValidationException;
import com.example.invoicepaymentapi.domain.exception.ValidationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 企業名値オブジェクト
 */
public record CompanyName(String value) {
    private static final Logger log = LoggerFactory.getLogger(CompanyName.class);
    private static final int MAX_LENGTH = 255;

    /**
     * 新規作成時のファクトリメソッド
     * バリデーションを実施
     */
    public static CompanyName ofCreate(String value) {
        List<ValidationError> errors = new ArrayList<>();

        if (value == null || value.isEmpty()) {
            errors.add(ValidationError.required("companyName"));
        } else {
            if (value.length() > MAX_LENGTH) {
                errors.add(new ValidationError("companyName", "validation.companyName.maxLength"));
            }
        }

        if (!errors.isEmpty()) {
            throw new DomainValidationException(errors);
        }

        return new CompanyName(value);
    }

    /**
     * 既存データ取得時のファクトリメソッド
     * nullの場合はエラーログを出力して、valueがnullの値オブジェクトを返す（不正データの可能性）
     */
    public static CompanyName ofGet(String value) {
        if (value == null) {
            log.error("CompanyName cannot be null. Invalid data detected in database.");
        }
        return new CompanyName(value);
    }
}
