package com.example.invoicepaymentapi.domain.service;

import com.example.invoicepaymentapi.domain.exception.DomainValidationException;
import com.example.invoicepaymentapi.domain.exception.ValidationError;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * ドメイン層のバリデーションサービス
 * 複数の値オブジェクトのバリデーションを一括で実行し、全エラーを集約して返す
 */
public class DomainValidationService {

    /**
     * 複数のバリデーションを一括で実行し、エラーがあれば例外をスローする
     *
     * @param validators バリデーション関数の可変長引数
     * @throws DomainValidationException バリデーションエラーがある場合（全フィールドのエラーを一括で返す）
     */
    @SafeVarargs
    public static void validateAll(Supplier<List<ValidationError>>... validators) {
        List<ValidationError> allErrors = new ArrayList<>();
        for (Supplier<List<ValidationError>> validator : validators) {
            allErrors.addAll(validator.get());
        }
        if (!allErrors.isEmpty()) {
            throw new DomainValidationException(allErrors);
        }
    }
}

