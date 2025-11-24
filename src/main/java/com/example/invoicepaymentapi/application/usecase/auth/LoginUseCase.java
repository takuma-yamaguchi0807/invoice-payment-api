package com.example.invoicepaymentapi.application.usecase.auth;

import com.example.invoicepaymentapi.application.usecase.auth.dto.LoginRequestDto;
import com.example.invoicepaymentapi.application.usecase.auth.dto.LoginResponseDto;
import com.example.invoicepaymentapi.domain.exception.DomainValidationException;
import com.example.invoicepaymentapi.domain.exception.UnauthorizedException;
import com.example.invoicepaymentapi.domain.exception.ValidationError;
import com.example.invoicepaymentapi.domain.model.auth.AccessToken;
import com.example.invoicepaymentapi.domain.model.user.*;
import com.example.invoicepaymentapi.domain.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * ログインユースケース
 */
@Service
public class LoginUseCase {
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
        
        // 必須フィールド不足の場合は400エラーとして返す
        if (hasRequiredFieldErrors) {
            List<ValidationError> allErrors = new ArrayList<>();
            allErrors.addAll(emailErrors);
            allErrors.addAll(passwordErrors);
            throw new DomainValidationException(allErrors);
        }
        
        // 形式エラーの場合は401エラーとして返す（セキュリティ上の理由）
        if (!emailErrors.isEmpty() || !passwordErrors.isEmpty()) {
            throw new UnauthorizedException("Invalid email or password");
        }

        // バリデーション成功後、値オブジェクトを作成
        Email email = Email.create(requestDto.email());
        Password password = Password.create(requestDto.password());

        // ユーザーを検索
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        // パスワードを検証
        if (!user.password().verify(password)) {
            throw new UnauthorizedException("Invalid email or password");
        }

        // JWTアクセストークンを作成
        AccessToken accessToken = AccessToken.create(user.id());

        // レスポンスDTOを作成
        return LoginResponseDto.from(accessToken);
    }
}

