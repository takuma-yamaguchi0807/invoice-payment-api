package com.example.invoicepaymentapi.presentation.security;

import com.example.invoicepaymentapi.domain.model.auth.AccessToken;
import com.example.invoicepaymentapi.domain.model.user.UserId;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * JWT認証フィルター
 * リクエストヘッダーからJWTトークンを取得し、検証して認証情報を設定する
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            String token = extractToken(request);
            if (token != null) {
                Authentication authentication = authenticate(token);
                if (authentication != null) {
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (Exception e) {
            log.debug("JWT authentication failed: {}", e.getMessage());
            // 認証失敗時はSecurityContextに認証情報を設定しない
            // 後続のフィルターで認証が必要な場合は403が返される
        }

        filterChain.doFilter(request, response);
    }

    /**
     * リクエストヘッダーからJWTトークンを抽出
     *
     * @param request HTTPリクエスト
     * @return JWTトークン（存在しない場合はnull）
     */
    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }

    /**
     * JWTトークンを検証し、認証情報を作成
     *
     * @param token JWTトークン
     * @return 認証情報（検証失敗時はnull）
     */
    private Authentication authenticate(String token) {
        try {
            AccessToken accessToken = AccessToken.create(token);
            UserId userId = accessToken.extractUserId();

            // 認証情報を作成（権限は現時点ではROLE_USERのみ）
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userId.value(),
                            null,
                            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
                    );

            return authentication;
        } catch (Exception e) {
            log.debug("JWT token validation failed: {}", e.getMessage());
            return null;
        }
    }
}

