package com.example.invoicepaymentapi.presentation.web.invoices;

import com.example.invoicepaymentapi.application.usecase.invoices.CreateInvoiceUseCase;
import com.example.invoicepaymentapi.application.usecase.invoices.ListInvoicesUseCase;
import com.example.invoicepaymentapi.application.usecase.invoices.dto.CreateInvoiceResponseDto;
import com.example.invoicepaymentapi.application.usecase.invoices.dto.InvoiceListResponseDto;
import com.example.invoicepaymentapi.presentation.web.constants.ApiPropertyNames;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * 請求書コントローラー
 */
@RestController
@RequestMapping("/invoices")
@RequiredArgsConstructor
public class InvoiceController {
    private final CreateInvoiceUseCase createInvoiceUseCase;
    private final ListInvoicesUseCase listInvoicesUseCase;

    /**
     * 請求書登録
     *
     * @param request 請求書登録リクエスト
     * @return 201 Created（作成された請求書のIDを含む）
     */
    @PostMapping
    public ResponseEntity<CreateInvoiceResponse> createInvoice(
            @RequestBody CreateInvoiceRequest request
    ) {
        Integer userId = extractUserId();
        CreateInvoiceResponseDto responseDto = createInvoiceUseCase.execute(userId, request.toDto());
        CreateInvoiceResponse response = CreateInvoiceResponse.from(responseDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 請求書一覧取得
     *
     * @param paymentDueFrom 支払期日の開始日（未指定時は当日をデフォルト）
     * @param paymentDueTo 支払期日の終了日（未指定時は1ヶ月後をデフォルト）
     * @param pageNumber ページ番号（未指定時は1をデフォルト）
     * @param pageSize ページサイズ（未指定時は20をデフォルト）
     * @return 200 OK（請求書一覧を含む）
     */
    @GetMapping
    public ResponseEntity<InvoiceListResponse> listInvoices(
            @RequestParam(name = ApiPropertyNames.PAYMENT_DUE_FROM, required = false) String paymentDueFrom,
            @RequestParam(name = ApiPropertyNames.PAYMENT_DUE_TO, required = false) String paymentDueTo,
            @RequestParam(name = ApiPropertyNames.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(name = ApiPropertyNames.PAGE_SIZE, required = false) Integer pageSize
    ) {
        Integer userId = extractUserId();
        InvoiceListResponseDto responseDto = listInvoicesUseCase.execute(
                userId,
                paymentDueFrom,
                paymentDueTo,
                pageNumber,
                pageSize
        );
        InvoiceListResponse response = InvoiceListResponse.from(responseDto);
        return ResponseEntity.ok(response);
    }

    /**
     * SecurityContextから認証情報を取得し、ユーザーIDを抽出
     *
     * @return ユーザーID
     */
    private Integer extractUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new IllegalStateException("Authentication is required");
        }
        return (Integer) authentication.getPrincipal();
    }
}

