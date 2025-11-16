package com.example.invoicepaymentapi.application.usecase.invoices.dto;

import com.example.invoicepaymentapi.domain.model.invoice.InvoiceId;

/**
 * 請求書登録レスポンスDTO
 * domain層のInvoiceIdから受け取り、presentation層に渡すためのDTO
 */
public record CreateInvoiceResponseDto(
        Integer id
) {
    /**
     * InvoiceIdからDTOを作成
     *
     * @param invoiceId 請求書ID
     * @return CreateInvoiceResponseDto
     */
    public static CreateInvoiceResponseDto from(InvoiceId invoiceId) {
        return new CreateInvoiceResponseDto(invoiceId.value());
    }
}

