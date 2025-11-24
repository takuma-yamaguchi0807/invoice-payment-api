package com.example.invoicepaymentapi.domain.model.auth;

import com.example.invoicepaymentapi.domain.exception.UnauthorizedException;
import com.example.invoicepaymentapi.domain.model.user.UserId;
import org.apache.commons.lang3.StringUtils;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * JWTアクセストークン値オブジェクト
 * JWT生成・検証ロジックを提供
 */
public record AccessToken(String value) {
    private static final Logger log = LoggerFactory.getLogger(AccessToken.class);
    
    /**
     * システムプロパティから値を取得（spring-dotenvが.envファイルをシステムプロパティとして読み込んだ場合）
     * 取得できない場合は起動時エラーをスロー
     * 
     * 注意: 静的フィールドの初期化はSpringのコンテキスト構築前に実行されるため、
     * 遅延初期化（lazy initialization）を使用する
     */
    private static String getRequiredProperty(String key) {
        String value = System.getProperty(key);
        if (value == null || value.isEmpty()) {
            value = System.getenv(key);
        }
        if (value == null || value.isEmpty()) {
            throw new IllegalStateException(key + " environment variable is not set");
        }
        return value;
    }
    
    // 遅延初期化: 実際に使用するときに値を取得する
    private static String getJwtSecret() {
        return getRequiredProperty("JWT_SECRET");
    }
    
    private static String getJwtExpiration() {
        return getRequiredProperty("JWT_EXPIRATION");
    }

    /**
     * ユーザーIDからJWTアクセストークンを作成
     *
     * @param userId ユーザーID
     * @return 作成されたJWTアクセストークン
     */
    public static AccessToken create(UserId userId) {
        if (userId == null || userId.value() == null) {
            throw new IllegalArgumentException("UserId cannot be null");
        }

        String jwtExpiration = getJwtExpiration();
        if (StringUtils.isEmpty(jwtExpiration)) {
            throw new IllegalStateException("JWT_EXPIRATION environment variable is not set");
        }

        long expirationSeconds;
        try {
            expirationSeconds = Long.parseLong(jwtExpiration);
        } catch (NumberFormatException e) {
            throw new IllegalStateException("JWT_EXPIRATION environment variable has invalid value: " + jwtExpiration, e);
        }

        String jwtSecret = getJwtSecret();
        if (StringUtils.isEmpty(jwtSecret)) {
            throw new IllegalStateException("JWT_SECRET environment variable is not set");
        }
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        Instant now = Instant.now();
        Instant expiration = now.plus(expirationSeconds, ChronoUnit.SECONDS);

        String token = Jwts.builder()
                .subject(String.valueOf(userId.value()))
                .claim("userId", userId.value())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .signWith(key)
                .compact();

        return new AccessToken(token);
    }

    /**
     * JWT文字列を検証する（認証用）
     * 期限切れ、改ざん、形式不正などをチェックし、認証エラーの場合はUnauthorizedExceptionをスロー
     *
     * @param token JWT文字列
     * @throws IllegalStateException JWT_SECRET環境変数が設定されていない場合（システム設定エラー）
     * @throws UnauthorizedException トークンがnullまたは空の場合、またはJWT検証に失敗した場合（期限切れ、改ざん、形式不正など）
     */
    public static void validate(String token) {
        if (StringUtils.isEmpty(token)) {
            log.warn("Authentication failed: Access token is required");
            throw new UnauthorizedException("error.authentication.failed");
        }

        try {
            parseSignedClaims(token);
        } catch (IllegalStateException e) {
            // JWT_SECRET未設定の場合はそのまま再スロー（システム設定エラー）
            throw e;
        } catch (JwtException e) {
            log.warn("Authentication failed: JWT token validation failed. reason={}, token={}", e.getMessage(), token);
            throw new UnauthorizedException("error.authentication.failed");
        } catch (Exception e) {
            log.warn("Authentication failed: Unexpected error during JWT validation. reason={}, token={}", e.getMessage(), token);
            throw new UnauthorizedException("error.authentication.failed");
        }
    }

    /**
     * JWTからユーザーIDを抽出
     * このメソッドを呼び出す前に、validate()で検証済みであることを前提とする
     *
     * @return ユーザーID
     * @throws IllegalStateException JWT_SECRET環境変数が設定されていない場合、またはUserIdがJWTクレームに存在しない場合
     * @throws UnauthorizedException JWT検証に失敗した場合（期限切れ、改ざん、形式不正など）
     */
    public UserId extractUserId() {
        try {
            Jws<Claims> claims = parseSignedClaims(this.value);
            Integer userId = claims.getPayload().get("userId", Integer.class);
            if (userId == null) {
                throw new IllegalStateException("UserId not found in JWT claims");
            }
            return UserId.reconstruct(userId);
        } catch (IllegalStateException e) {
            // JWT_SECRET未設定またはUserId未存在の場合はそのまま再スロー
            throw e;
        } catch (JwtException e) {
            log.warn("Authentication failed: JWT token validation failed. reason={}, token={}", e.getMessage(), this.value);
            throw new UnauthorizedException("error.authentication.failed");
        } catch (Exception e) {
            log.warn("Authentication failed: Unexpected error during JWT parsing. reason={}, token={}", e.getMessage(), this.value);
            throw new UnauthorizedException("error.authentication.failed");
        }
    }

    /**
     * JWTトークンを検証してパースする
     *
     * @param token JWTトークン文字列
     * @return パースされたJWTクレーム
     * @throws IllegalStateException JWT_SECRET環境変数が設定されていない場合
     * @throws Exception JWT検証に失敗した場合
     */
    private static Jws<Claims> parseSignedClaims(String token) {
        String jwtSecret = getJwtSecret();
        if (StringUtils.isEmpty(jwtSecret)) {
            throw new IllegalStateException("JWT_SECRET environment variable is not set");
        }
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token);
    }
}

