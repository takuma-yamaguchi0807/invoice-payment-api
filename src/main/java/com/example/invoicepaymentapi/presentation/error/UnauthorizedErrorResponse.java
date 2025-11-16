package com.example.invoicepaymentapi.presentation.error;

/**
 * 認証エラーレスポンス
 * セキュリティ上の理由により、詳細情報は返さず、codeとmessageのみを返す
 */
public record UnauthorizedErrorResponse(
        String code,
        String message
) {
    /**
     * エラーコード
     */
    public static final String UNAUTHORIZED_ERROR_CODE = "UNAUTHORIZED";
}

