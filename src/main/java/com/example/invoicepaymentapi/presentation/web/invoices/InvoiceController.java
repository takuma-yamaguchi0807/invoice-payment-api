package com.example.invoicepaymentapi.presentation.web.invoices;

import com.example.invoicepaymentapi.application.usecase.invoices.CreateInvoiceUseCase;
import com.example.invoicepaymentapi.application.usecase.invoices.dto.CreateInvoiceResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 請求書コントローラー
 */
@RestController
@RequestMapping("/invoices")
public class InvoiceController {
    private final CreateInvoiceUseCase createInvoiceUseCase;

    public InvoiceController(CreateInvoiceUseCase createInvoiceUseCase) {
        this.createInvoiceUseCase = createInvoiceUseCase;
    }

    /**
     * 請求書登録
     *
     * @param userId ユーザーID（認証情報から取得する想定。暫定的にパスパラメータで受け取る）
     * @param request 請求書登録リクエスト
     * @return 201 Created（作成された請求書のIDを含む）
     */
    @PostMapping
    public ResponseEntity<CreateInvoiceResponse> createInvoice(
      //TODO: JWTからユーザーIDを取得する。
            @RequestHeader("X-User-Id") Integer userId,
            @RequestBody CreateInvoiceRequest request
    ) {
        CreateInvoiceResponseDto responseDto = createInvoiceUseCase.execute(userId, request.toDto());
        CreateInvoiceResponse response = CreateInvoiceResponse.from(responseDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}

