package com.example.invoicepaymentapi.presentation.web.invoices;

import com.example.invoicepaymentapi.application.usecase.invoices.dto.CreateInvoiceRequestDto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 請求書登録リクエスト
 */
public record CreateInvoiceRequest(
        LocalDate issueDate,
        BigDecimal paymentAmount,
        LocalDate paymentDueDate
) {
    /**
     * application層のDTOに変換
     *
     * @return CreateInvoiceRequestDto
     */
    public CreateInvoiceRequestDto toDto() {
        return new CreateInvoiceRequestDto(
                issueDate,
                paymentAmount,
                paymentDueDate
        );
    }
}

