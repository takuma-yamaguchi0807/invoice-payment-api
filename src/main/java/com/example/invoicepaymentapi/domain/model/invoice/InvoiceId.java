package com.example.invoicepaymentapi.domain.model.invoice;

import com.example.invoicepaymentapi.domain.exception.DomainValidationException;
import com.example.invoicepaymentapi.domain.exception.ValidationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 請求書ID値オブジェクト
 */
public record InvoiceId(Integer value) {
    private static final Logger log = LoggerFactory.getLogger(InvoiceId.class);
    /**
     * 新規作成時のファクトリメソッド
     * バリデーションを実施
     */
    public static InvoiceId ofCreate(Integer value) {
        List<ValidationError> errors = new ArrayList<>();

        if (value == null) {
            errors.add(ValidationError.required("invoiceId"));
        } else {
            // 0以下チェック
            if (value <= 0) {
                errors.add(new ValidationError("invoiceId", "validation.invoiceId.zeroOrNegative"));
            }
        }

        if (!errors.isEmpty()) {
            throw new DomainValidationException(errors);
        }

        return new InvoiceId(value);
    }

    /**
     * 既存データ取得時のファクトリメソッド
     * nullの場合はエラーログを出力して、valueがnullの値オブジェクトを返す（不正データの可能性）
     */
    public static InvoiceId ofGet(Integer value) {
        if (value == null) {
            log.error("InvoiceId cannot be null. Invalid data detected in database.");
        }
        return new InvoiceId(value);
    }
}
