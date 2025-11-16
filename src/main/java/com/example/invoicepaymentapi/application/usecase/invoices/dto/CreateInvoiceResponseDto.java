package com.example.invoicepaymentapi.application.usecase.invoices.dto;

import com.example.invoicepaymentapi.domain.model.invoice.Invoice;

/**
 * 請求書登録レスポンスDTO
 * domain層のInvoice集約ルートから受け取り、presentation層に渡すためのDTO
 */
public record CreateInvoiceResponseDto(
        Integer id
) {
    /**
     * Invoice集約ルートからDTOを作成
     *
     * @param invoice Invoice集約ルート
     * @return CreateInvoiceResponseDto
     */
    public static CreateInvoiceResponseDto from(Invoice invoice) {
        return new CreateInvoiceResponseDto(
                invoice.id() != null ? invoice.id().value() : null
        );
    }
}

