package com.example.invoicepaymentapi.domain.model.auth;

import com.example.invoicepaymentapi.domain.exception.DomainValidationException;
import com.example.invoicepaymentapi.domain.exception.ValidationError;
import com.example.invoicepaymentapi.domain.model.user.UserId;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * JWTアクセストークン値オブジェクト
 * JWT生成・検証ロジックを提供
 */
public record AccessToken(String value) {
    private static final Logger log = LoggerFactory.getLogger(AccessToken.class);
    private static final String JWT_SECRET = System.getenv("JWT_SECRET");
    private static final String JWT_EXPIRATION = System.getenv("JWT_EXPIRATION");
    private static final long DEFAULT_EXPIRATION_HOURS = 24;

    /**
     * ユーザーIDからJWTアクセストークンを生成
     *
     * @param userId ユーザーID
     * @return 生成されたJWTアクセストークン
     */
    public static AccessToken generate(UserId userId) {
        if (userId == null || userId.value() == null) {
            throw new IllegalArgumentException("UserId cannot be null");
        }

        if (JWT_SECRET == null || JWT_SECRET.isEmpty()) {
            throw new IllegalStateException("JWT_SECRET environment variable is not set");
        }

        long expirationHours = DEFAULT_EXPIRATION_HOURS;
        if (JWT_EXPIRATION != null && !JWT_EXPIRATION.isEmpty()) {
            try {
                expirationHours = Long.parseLong(JWT_EXPIRATION);
            } catch (NumberFormatException e) {
                log.warn("Invalid JWT_EXPIRATION value: {}. Using default: {} hours", JWT_EXPIRATION, DEFAULT_EXPIRATION_HOURS);
            }
        }

        SecretKey key = Keys.hmacShaKeyFor(JWT_SECRET.getBytes(StandardCharsets.UTF_8));
        Instant now = Instant.now();
        Instant expiration = now.plus(expirationHours, ChronoUnit.HOURS);

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
     * JWT文字列からAccessToken値オブジェクトを作成（検証付き）
     *
     * @param token JWT文字列
     * @return 検証済みのAccessToken値オブジェクト
     * @throws DomainValidationException JWTが無効な場合
     */
    public static AccessToken create(String token) {
        List<ValidationError> errors = validate(token);
        if (!errors.isEmpty()) {
            throw new DomainValidationException(errors);
        }
        return new AccessToken(token);
    }

    /**
     * JWT文字列を検証し、エラーのリストを返す
     *
     * @param token JWT文字列
     * @return バリデーションエラーのリスト（エラーがない場合は空のリスト）
     */
    public static List<ValidationError> validate(String token) {
        List<ValidationError> errors = new ArrayList<>();

        if (token == null || token.isEmpty()) {
            errors.add(ValidationError.required("accessToken"));
            return errors;
        }

        if (JWT_SECRET == null || JWT_SECRET.isEmpty()) {
            log.error("JWT_SECRET environment variable is not set");
            errors.add(new ValidationError("accessToken", "validation.accessToken.invalid"));
            return errors;
        }

        try {
            SecretKey key = Keys.hmacShaKeyFor(JWT_SECRET.getBytes(StandardCharsets.UTF_8));
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
        } catch (Exception e) {
            log.debug("JWT validation failed: {}", e.getMessage());
            errors.add(new ValidationError("accessToken", "validation.accessToken.invalid"));
        }

        return errors;
    }

    /**
     * JWTからユーザーIDを抽出
     *
     * @return ユーザーID
     * @throws IllegalStateException JWTが無効な場合
     */
    public UserId extractUserId() {
        if (JWT_SECRET == null || JWT_SECRET.isEmpty()) {
            throw new IllegalStateException("JWT_SECRET environment variable is not set");
        }

        try {
            SecretKey key = Keys.hmacShaKeyFor(JWT_SECRET.getBytes(StandardCharsets.UTF_8));
            var claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(this.value);

            Integer userId = claims.getPayload().get("userId", Integer.class);
            if (userId == null) {
                throw new IllegalStateException("UserId not found in JWT claims");
            }
            return UserId.reconstruct(userId);
        } catch (Exception e) {
            log.error("Failed to extract userId from JWT: {}", e.getMessage());
            throw new IllegalStateException("Invalid JWT token", e);
        }
    }
}

