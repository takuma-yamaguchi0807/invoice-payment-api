package com.example.invoicepaymentapi.presentation.error;

import com.example.invoicepaymentapi.presentation.web.constants.ApiPropertyNames;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * バリデーションエラーレスポンス
 * OpenAPI仕様に準拠
 * detailsはフィールド名をキー、エラーメッセージの配列を値とするMap形式
 */
public record ValidationErrorResponse(
        @JsonProperty(ApiPropertyNames.CODE)
        String code,
        @JsonProperty(ApiPropertyNames.DETAILS)
        Map<String, java.util.List<String>> details
) {
    /**
     * エラーコード
     */
    public static final String VALIDATION_ERROR_CODE = "VALIDATION_ERROR";
}

