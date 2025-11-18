package com.example.invoicepaymentapi.domain.shared.pagination;

import com.example.invoicepaymentapi.domain.exception.DomainValidationException;
import com.example.invoicepaymentapi.domain.exception.ValidationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * ページ番号値オブジェクト
 * 1以上である必要がある
 */
public record PageNumber(Integer value) {
    private static final Logger log = LoggerFactory.getLogger(PageNumber.class);
    private static final int MIN_VALUE = 1;
    public static final int DEFAULT_VALUE = 1;

    /**
     * 新規作成時のファクトリメソッド
     * バリデーションを実施
     *
     * @param value ページ番号
     * @return ページ番号値オブジェクト
     */
    public static PageNumber create(Integer value) {
        List<ValidationError> errors = validate(value);
        if (!errors.isEmpty()) {
            throw new DomainValidationException(errors);
        }
        return new PageNumber(value);
    }

    /**
     * デフォルト値でページ番号を作成
     *
     * @return デフォルト値（1）のページ番号値オブジェクト
     */
    public static PageNumber defaultValue() {
        return new PageNumber(DEFAULT_VALUE);
    }

    /**
     * nullの場合はデフォルト値、それ以外はバリデーションして作成
     *
     * @param value ページ番号（nullの場合はデフォルト値を使用）
     * @return ページ番号値オブジェクト
     */
    public static PageNumber ofCreateOrDefault(Integer value) {
        if (value == null) {
            return defaultValue();
        }
        return create(value);
    }

    /**
     * バリデーションを実行し、エラーのリストを返す
     * 例外を投げずにエラーを返すため、複数のフィールドのバリデーションを一括で実行できる
     *
     * @param value ページ番号
     * @return バリデーションエラーのリスト（エラーがない場合は空のリスト）
     */
    public static List<ValidationError> validate(Integer value) {
        List<ValidationError> errors = new ArrayList<>();

        if (value == null) {
            errors.add(ValidationError.required("page_number"));
        } else {
            if (value < MIN_VALUE) {
                errors.add(new ValidationError("page_number", "validation.pageNumber.min"));
            }
        }

        return errors;
    }

    /**
     * 既存データ取得時のファクトリメソッド
     * nullの場合はエラーログを出力して、valueがnullの値オブジェクトを返す（不正データの可能性）
     */
    public static PageNumber reconstruct(Integer value) {
        if (value == null) {
            log.error("PageNumber cannot be null. Invalid data detected in database.");
        }
        return new PageNumber(value);
    }
}

