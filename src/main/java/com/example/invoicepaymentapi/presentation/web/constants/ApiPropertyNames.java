package com.example.invoicepaymentapi.presentation.web.constants;

/**
 * APIのプロパティ名定数クラス
 * OpenAPI仕様に合わせたプロパティ名を一元管理
 */
public final class ApiPropertyNames {
    private ApiPropertyNames() {
        throw new AssertionError("Utility class should not be instantiated");
    }

    // ユーザー登録リクエスト
    public static final String COMPANY_NAME = "companyName";
    public static final String NAME = "name";
    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";

    // ログインリクエスト/レスポンス
    public static final String ACCESS_TOKEN = "accessToken";

    // 請求書登録リクエスト
    public static final String ISSUE_DATE = "issueDate";
    public static final String PAYMENT_AMOUNT = "paymentAmount";
    public static final String PAYMENT_DUE_DATE = "paymentDueDate";

    // 請求書登録レスポンス
    public static final String ID = "id";

    // 請求書一覧取得クエリパラメータ
    public static final String PAYMENT_DUE_FROM = "paymentDueFrom";
    public static final String PAYMENT_DUE_TO = "paymentDueTo";
    public static final String PAGE_NUMBER = "page_number";
    public static final String PAGE_SIZE = "page_size";

    // 請求書一覧レスポンス
    public static final String ITEMS = "items";
    public static final String PAGINATION = "pagination";
    public static final String TOTAL_AMOUNT = "totalAmount";
    public static final String TOTAL = "total";
    public static final String TOTAL_PAGES = "total_pages";

    // エラーレスポンス共通
    public static final String CODE = "code";
    public static final String MESSAGE = "message";
    public static final String DETAILS = "details";
}

