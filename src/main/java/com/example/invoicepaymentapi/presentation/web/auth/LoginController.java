package com.example.invoicepaymentapi.presentation.web.auth;

import com.example.invoicepaymentapi.application.usecase.auth.LoginUseCase;
import com.example.invoicepaymentapi.application.usecase.auth.dto.LoginResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 認証コントローラー
 */
@RestController
@RequestMapping("/auth")
public class LoginController {
    private final LoginUseCase loginUseCase;

    public LoginController(LoginUseCase loginUseCase) {
        this.loginUseCase = loginUseCase;
    }

    /**
     * ログイン
     *
     * @param request ログインリクエスト
     * @return 200 OK（JWTアクセストークンを含む）
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponseDto responseDto = loginUseCase.execute(request.toDto());
        LoginResponse response = LoginResponse.from(responseDto);
        return ResponseEntity.ok(response);
    }
}

