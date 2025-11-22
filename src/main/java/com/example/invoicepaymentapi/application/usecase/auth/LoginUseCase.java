package com.example.invoicepaymentapi.application.usecase.auth;

import com.example.invoicepaymentapi.application.usecase.auth.dto.LoginRequestDto;
import com.example.invoicepaymentapi.application.usecase.auth.dto.LoginResponseDto;
import com.example.invoicepaymentapi.domain.exception.DomainValidationException;
import com.example.invoicepaymentapi.domain.exception.UnauthorizedException;
import com.example.invoicepaymentapi.domain.model.auth.AccessToken;
import com.example.invoicepaymentapi.domain.model.user.*;
import com.example.invoicepaymentapi.domain.repository.UserRepository;
import com.example.invoicepaymentapi.domain.service.DomainValidationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        // 全フィールドのバリデーションを一括で実行
        DomainValidationService.validateAll(
                () -> Email.validate(requestDto.email()),
                () -> Password.validate(requestDto.password())
        );

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

