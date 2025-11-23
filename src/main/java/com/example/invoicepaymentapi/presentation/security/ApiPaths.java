package com.example.invoicepaymentapi.presentation.security;

/**
 * APIパス定数
 * context-pathを含む完全なパスと、context-pathを除いたパスの両方を定義
 */
public final class ApiPaths {
    /**
     * APIのベースパス（context-path）
     */
    public static final String BASE_PATH = "/api/v1";

    /**
     * 認証エンドポイント（context-pathを含む完全なパス）
     * AntPathRequestMatcherで使用
     */
    public static final String AUTH_LOGIN_FULL = BASE_PATH + "/auth/login";

    /**
     * 認証エンドポイント（context-pathを除いたパス）
     * Spring SecurityのrequestMatchers()で使用
     */
    public static final String AUTH_LOGIN = "/auth/login";

    /**
     * ユーザー登録エンドポイント（context-pathを含む完全なパス）
     * AntPathRequestMatcherで使用
     */
    public static final String USERS_FULL = BASE_PATH + "/users";

    /**
     * ユーザー登録エンドポイント（context-pathを除いたパス）
     * Spring SecurityのrequestMatchers()で使用
     */
    public static final String USERS = "/users";

    private ApiPaths() {
        // インスタンス化を防ぐ
    }
}

