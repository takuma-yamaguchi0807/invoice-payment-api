package com.example.invoicepaymentapi.application.usecase.auth;

import com.example.invoicepaymentapi.application.usecase.auth.dto.LoginRequestDto;
import com.example.invoicepaymentapi.application.usecase.auth.dto.LoginResponseDto;
import com.example.invoicepaymentapi.domain.exception.DomainValidationException;
import com.example.invoicepaymentapi.domain.exception.UnauthorizedException;
import com.example.invoicepaymentapi.domain.exception.ValidationError;
import com.example.invoicepaymentapi.domain.model.auth.AccessToken;
import com.example.invoicepaymentapi.domain.model.user.*;
import com.example.invoicepaymentapi.domain.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * ログインユースケース
 */
@Service
public class LoginUseCase {
    private static final Logger log = LoggerFactory.getLogger(LoginUseCase.class);
    private final UserRepository userRepository;

    public LoginUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * ログインを実行する
     *
     * @param requestDto ログインリクエストDTO
     * @return ログインレスポンスDTO（JWTアクセストークンを含む）
     * @throws DomainValidationException バリデーションエラーがある場合
     * @throws UnauthorizedException 認証に失敗した場合（メールアドレスまたはパスワードが不正）
     */
    @Transactional(readOnly = true)
    public LoginResponseDto execute(LoginRequestDto requestDto) {
        // バリデーションを実行（必須フィールドチェックと形式チェックを分離）
        List<ValidationError> emailErrors = Email.validate(requestDto.email());
        List<ValidationError> passwordErrors = Password.validate(requestDto.password());
        
        // 必須フィールド不足のエラーをチェック
        boolean hasRequiredFieldErrors = emailErrors.stream().anyMatch(e -> e.messageKey().equals(ValidationError.REQUIRED_MESSAGE_KEY))
                || passwordErrors.stream().anyMatch(e -> e.messageKey().equals(ValidationError.REQUIRED_MESSAGE_KEY));
        
        // 形式エラーと同時に返却されないように、必須エラーがある場合はそれだけを返す
        if (hasRequiredFieldErrors) {
            List<ValidationError> requiredErrors = new ArrayList<>();
            // 必須エラーのみを抽出
            emailErrors.stream()
                    .filter(e -> e.messageKey().equals(ValidationError.REQUIRED_MESSAGE_KEY))
                    .forEach(requiredErrors::add);
            passwordErrors.stream()
                    .filter(e -> e.messageKey().equals(ValidationError.REQUIRED_MESSAGE_KEY))
                    .forEach(requiredErrors::add);
            throw new DomainValidationException(requiredErrors);
        }
        
        // 形式エラーの場合は401エラーとして返す（セキュリティ上の理由）
        if (!emailErrors.isEmpty() || !passwordErrors.isEmpty()) {
            if (!emailErrors.isEmpty()) {
                log.warn("Login failed: Invalid email format. email={}", requestDto.email());
            }
            if (!passwordErrors.isEmpty()) {
                log.warn("Login failed: Invalid password format. email={}", requestDto.email());
            }
            throw new UnauthorizedException("error.authentication.failed");
        }

        // バリデーション成功後、値オブジェクトを作成
        Email email = Email.create(requestDto.email());
        Password password = Password.create(requestDto.password());

        // ユーザーを検索
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Login failed: User not found. email={}", requestDto.email());
                    return new UnauthorizedException("error.authentication.failed");
                });

        // パスワードを検証
        if (!user.password().verify(password)) {
            log.warn("Login failed: Password mismatch. email={}, userId={}", requestDto.email(), user.id().value());
            throw new UnauthorizedException("error.authentication.failed");
        }

        // JWTアクセストークンを作成
        AccessToken accessToken = AccessToken.create(user.id());

        // レスポンスDTOを作成
        return LoginResponseDto.from(accessToken);
    }
}

