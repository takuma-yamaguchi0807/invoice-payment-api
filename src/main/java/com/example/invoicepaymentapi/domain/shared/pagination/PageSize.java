package com.example.invoicepaymentapi.domain.shared.pagination;

import com.example.invoicepaymentapi.domain.exception.DomainValidationException;
import com.example.invoicepaymentapi.domain.exception.ValidationError;
import com.example.invoicepaymentapi.presentation.web.constants.ApiPropertyNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * ページサイズ値オブジェクト
 * 1以上100以下である必要がある
 */
public record PageSize(Integer value) {
    private static final Logger log = LoggerFactory.getLogger(PageSize.class);
    private static final int MIN_VALUE = 1;
    private static final int MAX_VALUE = 100;
    public static final int DEFAULT_VALUE = 20;

    /**
     * 新規作成時のファクトリメソッド
     * バリデーションを実施
     *
     * @param value ページサイズ
     * @return ページサイズ値オブジェクト
     */
    public static PageSize create(Integer value) {
        List<ValidationError> errors = validate(value);
        if (!errors.isEmpty()) {
            throw new DomainValidationException(errors);
        }
        return new PageSize(value);
    }

    /**
     * デフォルト値でページサイズを作成
     *
     * @return デフォルト値（20）のページサイズ値オブジェクト
     */
    public static PageSize defaultValue() {
        return new PageSize(DEFAULT_VALUE);
    }

    /**
     * nullの場合はデフォルト値、それ以外はバリデーションして作成
     *
     * @param value ページサイズ（nullの場合はデフォルト値を使用）
     * @return ページサイズ値オブジェクト
     */
    public static PageSize ofCreateOrDefault(Integer value) {
        if (value == null) {
            return defaultValue();
        }
        return create(value);
    }

    /**
     * バリデーションを実行し、エラーのリストを返す
     * 例外を投げずにエラーを返すため、複数のフィールドのバリデーションを一括で実行できる
     *
     * @param value ページサイズ
     * @return バリデーションエラーのリスト（エラーがない場合は空のリスト）
     */
    public static List<ValidationError> validate(Integer value) {
        List<ValidationError> errors = new ArrayList<>();

        if (value == null) {
            errors.add(ValidationError.required(ApiPropertyNames.PAGE_SIZE));
        } else {
            if (value < MIN_VALUE) {
                errors.add(new ValidationError(ApiPropertyNames.PAGE_SIZE, "validation.pageSize.min"));
            }
            if (value > MAX_VALUE) {
                errors.add(new ValidationError(ApiPropertyNames.PAGE_SIZE, "validation.pageSize.max"));
            }
        }

        return errors;
    }

    /**
     * 既存データ取得時のファクトリメソッド
     * nullの場合はエラーログを出力して、valueがnullの値オブジェクトを返す（不正データの可能性）
     */
    public static PageSize reconstruct(Integer value) {
        if (value == null) {
            log.error("PageSize cannot be null. Invalid data detected in database.");
        }
        return new PageSize(value);
    }
}

