package com.example.invoicepaymentapi.presentation.web.invoices;

import com.example.invoicepaymentapi.application.usecase.invoices.dto.CreateInvoiceResponseDto;

/**
 * 請求書登録レスポンス
 */
public record CreateInvoiceResponse(
        Integer id
) {
    /**
     * application層のDTOからResponseを作成
     *
     * @param dto CreateInvoiceResponseDto
     * @return CreateInvoiceResponse
     */
    public static CreateInvoiceResponse from(CreateInvoiceResponseDto dto) {
        return new CreateInvoiceResponse(dto.id());
    }
}

