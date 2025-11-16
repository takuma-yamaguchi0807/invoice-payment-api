package com.example.invoicepaymentapi.presentation.error;

/**
 * コンフリクトエラーレスポンス
 * emailが既に存在する場合など、リソースの競合を表す
 */
public record ConflictErrorResponse(
        String code,
        String message
) {
    /**
     * エラーコード
     */
    public static final String CONFLICT_ERROR_CODE = "CONFLICT";
}

