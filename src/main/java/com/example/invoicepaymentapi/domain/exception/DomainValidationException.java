package com.example.invoicepaymentapi.domain.exception;

import java.util.ArrayList;
import java.util.List;

/**
 * ドメイン層のバリデーション例外
 * 複数のバリデーションエラーを保持できる
 */
public class DomainValidationException extends RuntimeException {
    private final List<ValidationError> errors;

    /**
     * コンストラクタ
     *
     * @param errors バリデーションエラーのリスト
     */
    public DomainValidationException(List<ValidationError> errors) {
        super("Validation failed");
        this.errors = new ArrayList<>(errors);
    }

    /**
     * 単一のバリデーションエラーで例外を作成
     *
     * @param error バリデーションエラー
     */
    public DomainValidationException(ValidationError error) {
        this(List.of(error));
    }

    /**
     * バリデーションエラーのリストを取得
     *
     * @return バリデーションエラーのリスト
     */
    public List<ValidationError> getErrors() {
        return new ArrayList<>(errors);
    }
}

