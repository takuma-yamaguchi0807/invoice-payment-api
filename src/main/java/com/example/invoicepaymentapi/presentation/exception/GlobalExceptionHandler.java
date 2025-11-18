package com.example.invoicepaymentapi.presentation.exception;

import com.example.invoicepaymentapi.domain.exception.ConflictException;
import com.example.invoicepaymentapi.domain.exception.DomainValidationException;
import com.example.invoicepaymentapi.domain.exception.UnauthorizedException;
import com.example.invoicepaymentapi.domain.exception.ValidationError;
import com.example.invoicepaymentapi.presentation.error.ConflictErrorResponse;
import com.example.invoicepaymentapi.presentation.error.UnauthorizedErrorResponse;
import com.example.invoicepaymentapi.presentation.error.ValidationErrorResponse;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * グローバル例外ハンドラー
 * ドメイン層のバリデーション例外をキャッチして、ValidationErrorResponseに変換
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    private final MessageSource messageSource;

    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    /**
     * ドメイン層のバリデーション例外をハンドリング
     *
     * @param ex ドメイン層のバリデーション例外
     * @param locale ロケール
     * @return ValidationErrorResponse
     */
    @ExceptionHandler(DomainValidationException.class)
    public ResponseEntity<ValidationErrorResponse> handleDomainValidationException(
            DomainValidationException ex,
            Locale locale
    ) {
        // フィールドごとにエラーをグループ化し、メッセージのリストに変換
        Map<String, List<String>> details = ex.getErrors().stream()
                .collect(Collectors.groupingBy(
                        ValidationError::field,
                        Collectors.mapping(
                                error -> messageSource.getMessage(
                                        error.messageKey(),
                                        null,
                                        error.messageKey(), // メッセージが見つからない場合はキーをそのまま返す
                                        locale
                                ),
                                Collectors.toList()
                        )
                ));

        ValidationErrorResponse response = new ValidationErrorResponse(
                ValidationErrorResponse.VALIDATION_ERROR_CODE,
                details
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * リソース競合例外をハンドリング
     *
     * @param ex リソース競合例外
     * @return ConflictErrorResponse
     */
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ConflictErrorResponse> handleConflictException(
            ConflictException ex
    ) {
        ConflictErrorResponse response = new ConflictErrorResponse(
                ConflictErrorResponse.CONFLICT_ERROR_CODE,
                ex.getMessage()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    /**
     * 認証失敗例外をハンドリング
     *
     * @param ex 認証失敗例外
     * @return UnauthorizedErrorResponse
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<UnauthorizedErrorResponse> handleUnauthorizedException(
            UnauthorizedException ex
    ) {
        UnauthorizedErrorResponse response = new UnauthorizedErrorResponse(
                UnauthorizedErrorResponse.UNAUTHORIZED_ERROR_CODE,
                "Authentication failed"
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }
}

