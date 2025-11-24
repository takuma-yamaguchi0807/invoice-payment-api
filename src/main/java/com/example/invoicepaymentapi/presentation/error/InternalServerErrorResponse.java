package com.example.invoicepaymentapi.presentation.error;

import com.example.invoicepaymentapi.presentation.web.constants.ApiPropertyNames;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * サーバー内部エラーレスポンス
 * 予期しない例外が発生した場合に返す
 */
public record InternalServerErrorResponse(
        @JsonProperty(ApiPropertyNames.CODE)
        String code,
        @JsonProperty(ApiPropertyNames.MESSAGE)
        String message
) {
    /**
     * エラーコード
     */
    public static final String INTERNAL_SERVER_ERROR_CODE = "INTERNAL_SERVER_ERROR";
}

