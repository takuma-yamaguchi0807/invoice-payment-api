package com.example.invoicepaymentapi.presentation.security;

import com.example.invoicepaymentapi.domain.exception.UnauthorizedException;
import com.example.invoicepaymentapi.domain.model.auth.AccessToken;
import com.example.invoicepaymentapi.domain.model.user.User;
import com.example.invoicepaymentapi.domain.model.user.UserId;
import com.example.invoicepaymentapi.domain.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * JWT認証フィルターの単体テスト
 */
@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private User testUser;

    @BeforeAll
    static void setUpEnvironment() {
        // テスト用の環境変数を設定
        System.setProperty("JWT_SECRET", "test-secret-key-for-unit-testing-purposes-only-minimum-length-required");
        System.setProperty("JWT_EXPIRATION", "3600");
    }

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        testUser = User.reconstruct(
                UserId.reconstruct(1),
                com.example.invoicepaymentapi.domain.model.user.CompanyName.reconstruct("テスト会社"),
                com.example.invoicepaymentapi.domain.model.user.UserName.reconstruct("テストユーザー"),
                com.example.invoicepaymentapi.domain.model.user.Email.reconstruct("test@example.com"),
                com.example.invoicepaymentapi.domain.model.user.HashedPassword.reconstruct("hashed"),
                java.time.LocalDateTime.now(),
                java.time.LocalDateTime.now()
        );
    }

    @Nested
    @DisplayName("extractTokenメソッド（doFilterInternal経由）")
    class ExtractTokenTest {
        @Test
        @DisplayName("Authorizationヘッダーからトークンを正しく抽出できる")
        void shouldExtractTokenFromAuthorizationHeader() throws Exception {
            // Given
            AccessToken accessToken = AccessToken.create(UserId.reconstruct(1));
            when(request.getHeader("Authorization")).thenReturn("Bearer " + accessToken.value());
            when(userRepository.findById(any(UserId.class))).thenReturn(Optional.of(testUser));

            // When
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            // Then
            verify(filterChain).doFilter(request, response);
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            assertThat(authentication).isNotNull();
            assertThat(authentication.getPrincipal()).isEqualTo(1);
        }

        @Test
        @DisplayName("Bearerプレフィックスが正しく処理される")
        void shouldHandleBearerPrefix() throws Exception {
            // Given
            AccessToken accessToken = AccessToken.create(UserId.reconstruct(1));
            when(request.getHeader("Authorization")).thenReturn("Bearer " + accessToken.value());
            when(userRepository.findById(any(UserId.class))).thenReturn(Optional.of(testUser));

            // When
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            // Then
            verify(filterChain).doFilter(request, response);
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            assertThat(authentication).isNotNull();
        }

        @Test
        @DisplayName("ヘッダーが存在しない場合はnullを返す")
        void shouldReturnNullWhenHeaderNotExists() throws Exception {
            // Given
            when(request.getHeader("Authorization")).thenReturn(null);

            // When
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            // Then
            verify(filterChain).doFilter(request, response);
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            assertThat(authentication).isNull();
        }

        @Test
        @DisplayName("不正な形式のヘッダーの場合、nullを返す")
        void shouldReturnNullWhenInvalidHeaderFormat() throws Exception {
            // Given
            when(request.getHeader("Authorization")).thenReturn("InvalidFormat");

            // When
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            // Then
            verify(filterChain).doFilter(request, response);
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            assertThat(authentication).isNull();
        }
    }

    @Nested
    @DisplayName("authenticateメソッド（doFilterInternal経由）")
    class AuthenticateTest {
        @Test
        @DisplayName("有効なJWTトークンで認証が成功する")
        void shouldAuthenticateWithValidToken() throws Exception {
            // Given
            AccessToken accessToken = AccessToken.create(UserId.reconstruct(1));
            when(request.getHeader("Authorization")).thenReturn("Bearer " + accessToken.value());
            when(userRepository.findById(any(UserId.class))).thenReturn(Optional.of(testUser));

            // When
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            // Then
            verify(filterChain).doFilter(request, response);
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            assertThat(authentication).isNotNull();
            assertThat(authentication.getPrincipal()).isEqualTo(1);
        }

        @Test
        @DisplayName("無効なJWTトークンでUnauthorizedExceptionがスローされる")
        void shouldThrowUnauthorizedExceptionWithInvalidToken() throws Exception {
            // Given
            when(request.getHeader("Authorization")).thenReturn("Bearer invalid-token");

            // When & Then
            assertThatThrownBy(() -> jwtAuthenticationFilter.doFilterInternal(request, response, filterChain))
                    .isInstanceOf(AuthenticationCredentialsNotFoundException.class);
            verify(filterChain, never()).doFilter(request, response);
        }

        @Test
        @DisplayName("tokenがnullの場合はnullを返す（permitAll()に委譲）")
        void shouldReturnNullWhenTokenIsNull() throws Exception {
            // Given
            when(request.getHeader("Authorization")).thenReturn(null);

            // When
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            // Then
            verify(filterChain).doFilter(request, response);
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            assertThat(authentication).isNull();
        }

        @Test
        @DisplayName("ユーザーが存在しない場合にUnauthorizedExceptionがスローされる")
        void shouldThrowUnauthorizedExceptionWhenUserNotFound() throws Exception {
            // Given
            AccessToken accessToken = AccessToken.create(UserId.reconstruct(999));
            when(request.getHeader("Authorization")).thenReturn("Bearer " + accessToken.value());
            when(userRepository.findById(any(UserId.class))).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> jwtAuthenticationFilter.doFilterInternal(request, response, filterChain))
                    .isInstanceOf(AuthenticationCredentialsNotFoundException.class);
            verify(filterChain, never()).doFilter(request, response);
        }
    }

    @Nested
    @DisplayName("doFilterInternalメソッド")
    class DoFilterInternalTest {
        @Test
        @DisplayName("認証成功時にSecurityContextに認証情報が設定される")
        void shouldSetAuthenticationInSecurityContext() throws Exception {
            // Given
            AccessToken accessToken = AccessToken.create(UserId.reconstruct(1));
            when(request.getHeader("Authorization")).thenReturn("Bearer " + accessToken.value());
            when(userRepository.findById(any(UserId.class))).thenReturn(Optional.of(testUser));

            // When
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            // Then
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            assertThat(authentication).isNotNull();
            assertThat(authentication.getPrincipal()).isEqualTo(1);
            verify(filterChain).doFilter(request, response);
        }

        @Test
        @DisplayName("認証失敗時にUnauthorizedExceptionがAuthenticationExceptionに変換される")
        void shouldConvertUnauthorizedExceptionToAuthenticationException() throws Exception {
            // Given
            when(request.getHeader("Authorization")).thenReturn("Bearer invalid-token");

            // When & Then
            assertThatThrownBy(() -> jwtAuthenticationFilter.doFilterInternal(request, response, filterChain))
                    .isInstanceOf(AuthenticationCredentialsNotFoundException.class)
                    .hasCauseInstanceOf(UnauthorizedException.class);
            verify(filterChain, never()).doFilter(request, response);
        }

        @Test
        @DisplayName("認証不要なリクエスト（tokenがnull）でもfilterChainが実行される")
        void shouldExecuteFilterChainWhenTokenIsNull() throws Exception {
            // Given
            when(request.getHeader("Authorization")).thenReturn(null);

            // When
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            // Then
            verify(filterChain).doFilter(request, response);
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            assertThat(authentication).isNull();
        }

        @Test
        @DisplayName("予期しない例外が発生した場合でもfilterChainが実行される")
        void shouldExecuteFilterChainWhenUnexpectedExceptionOccurs() throws Exception {
            // Given
            when(request.getHeader("Authorization")).thenThrow(new RuntimeException("Unexpected error"));

            // When
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            // Then
            verify(filterChain).doFilter(request, response);
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            assertThat(authentication).isNull();
        }
    }
}

