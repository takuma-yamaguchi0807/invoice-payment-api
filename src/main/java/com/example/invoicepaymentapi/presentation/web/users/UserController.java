package com.example.invoicepaymentapi.presentation.web.users;

import com.example.invoicepaymentapi.application.usecase.users.RegisterUserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ユーザーコントローラー
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final RegisterUserUseCase registerUserUseCase;

    /**
     * ユーザー登録
     *
     * @param request ユーザー登録リクエスト
     * @return 201 Created（レスポンスボディなし）
     */
    @PostMapping
    public ResponseEntity<Void> registerUser(@RequestBody RegisterUserRequest request) {
        registerUserUseCase.execute(request.toDto());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}

