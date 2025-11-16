package com.example.invoicepaymentapi.application.usecase.users;

import com.example.invoicepaymentapi.application.usecase.users.dto.RegisterUserRequestDto;
import com.example.invoicepaymentapi.domain.exception.ConflictException;
import com.example.invoicepaymentapi.domain.exception.DomainValidationException;
import com.example.invoicepaymentapi.domain.exception.ValidationError;
import com.example.invoicepaymentapi.domain.model.user.*;
import com.example.invoicepaymentapi.domain.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * ユーザー登録ユースケース
 */
@Service
public class RegisterUserUseCase {
    private final UserRepository userRepository;

    public RegisterUserUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * ユーザーを登録する
     *
     * @param requestDto ユーザー登録リクエストDTO
     * @throws DomainValidationException バリデーションエラーがある場合（全フィールドのエラーを一括で返す）
     * @throws ConflictException メールアドレスが既に存在する場合
     */
    @Transactional
    public void execute(RegisterUserRequestDto requestDto) {
        // 全フィールドのバリデーションを一括で実行
        List<ValidationError> allErrors = new ArrayList<>();
        allErrors.addAll(CompanyName.validate(requestDto.companyName()));
        allErrors.addAll(UserName.validate(requestDto.name()));
        allErrors.addAll(Email.validate(requestDto.email()));
        allErrors.addAll(Password.validate(requestDto.password()));

        // エラーがあれば一括で例外を投げる
        if (!allErrors.isEmpty()) {
            throw new DomainValidationException(allErrors);
        }

        // バリデーション成功後、値オブジェクトを作成
        CompanyName companyName = CompanyName.ofCreate(requestDto.companyName());
        UserName name = UserName.ofCreate(requestDto.name());
        Email email = Email.ofCreate(requestDto.email());
        Password password = Password.ofCreate(requestDto.password());

        // メールアドレスの重複チェック
        if (userRepository.findByEmail(email).isPresent()) {
            throw new ConflictException("Email already exists: " + email.value());
        }

        // パスワードをハッシュ化
        HashedPassword hashedPassword = HashedPassword.ofCreate(password);

        // ユーザー集約ルートを作成
        User user = User.ofCreate(companyName, name, email, hashedPassword);

        // ユーザーを保存
        userRepository.save(user);
    }
}

