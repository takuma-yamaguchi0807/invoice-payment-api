package com.example.invoicepaymentapi.presentation.security;

import com.example.invoicepaymentapi.presentation.error.UnauthorizedErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 認証エラーハンドラー
 * 認証が必要なエンドポイントに未認証でアクセスした場合に401 Unauthorizedを返す
 */
@Component
public class SecurityExceptionHandler implements AuthenticationEntryPoint {
    private static final Logger log = LoggerFactory.getLogger(SecurityExceptionHandler.class);

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {
        log.debug("Authentication failed: {}", authException.getMessage());

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        UnauthorizedErrorResponse errorResponse = new UnauthorizedErrorResponse(
                UnauthorizedErrorResponse.UNAUTHORIZED_ERROR_CODE,
                "Authentication required"
        );

        // JSONレスポンスを書き込む
        response.getWriter().write(
                String.format(
                        "{\"code\":\"%s\",\"message\":\"%s\"}",
                        errorResponse.code(),
                        errorResponse.message()
                )
        );
    }
}

