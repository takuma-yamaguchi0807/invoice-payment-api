package com.example.invoicepaymentapi.presentation.web.invoices;

import com.example.invoicepaymentapi.application.usecase.invoices.dto.CreateInvoiceRequestDto;
import com.example.invoicepaymentapi.presentation.web.constants.ApiPropertyNames;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 請求書登録リクエスト
 */
public record CreateInvoiceRequest(
        @JsonProperty(ApiPropertyNames.ISSUE_DATE)
        LocalDate issueDate,
        @JsonProperty(ApiPropertyNames.PAYMENT_AMOUNT)
        BigDecimal paymentAmount,
        @JsonProperty(ApiPropertyNames.PAYMENT_DUE_DATE)
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

