package com.example.invoicepaymentapi.application.usecase.users;

import com.example.invoicepaymentapi.application.usecase.users.dto.RegisterUserRequestDto;
import com.example.invoicepaymentapi.domain.exception.ConflictException;
import com.example.invoicepaymentapi.domain.model.user.*;
import com.example.invoicepaymentapi.domain.repository.UserRepository;
import com.example.invoicepaymentapi.domain.service.DomainValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ユーザー登録ユースケース
 */
@Service
@RequiredArgsConstructor
public class RegisterUserUseCase {
    private final UserRepository userRepository;

    /**
     * ユーザーを登録する
     *
     * @param requestDto ユーザー登録リクエストDTO
     * @throws com.example.invoicepaymentapi.domain.exception.DomainValidationException バリデーションエラーがある場合（全フィールドのエラーを一括で返す）
     * @throws ConflictException メールアドレスが既に存在する場合
     */
    @Transactional
    public void execute(RegisterUserRequestDto requestDto) {
        // 全フィールドのバリデーションを一括で実行
        DomainValidationService.validateAll(
            () -> CompanyName.validate(requestDto.companyName()),
            () -> UserName.validate(requestDto.name()),
            () -> Email.validate(requestDto.email()),
            () -> Password.validate(requestDto.password())
        );

        // バリデーション成功後、値オブジェクトを作成
        CompanyName companyName = CompanyName.create(requestDto.companyName());
        UserName name = UserName.create(requestDto.name());
        Email email = Email.create(requestDto.email());
        Password password = Password.create(requestDto.password());

        // メールアドレスの重複チェック
        if (userRepository.findByEmail(email).isPresent()) {
            throw new ConflictException("error.conflict.email.exists");
        }

        // パスワードをハッシュ化
        HashedPassword hashedPassword = HashedPassword.create(password);

        // ユーザー集約ルートを作成
        User user = User.create(companyName, name, email, hashedPassword);

        // ユーザーを保存
        userRepository.save(user);
    }
}

