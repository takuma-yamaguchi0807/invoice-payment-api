package com.example.invoicepaymentapi.presentation.web.invoices;

import com.example.invoicepaymentapi.application.usecase.invoices.dto.CreateInvoiceRequestDto;
import com.example.invoicepaymentapi.presentation.web.constants.ApiPropertyNames;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

/**
 * 請求書登録リクエスト
 * Presentation層ではJSONから受け取る値をStringとして扱う
 * Domain層の値オブジェクトでバリデーションと変換を行う
 */
public record CreateInvoiceRequest(
        @JsonProperty(ApiPropertyNames.ISSUE_DATE)
        String issueDate,
        @JsonProperty(ApiPropertyNames.PAYMENT_AMOUNT)
        BigDecimal paymentAmount,
        @JsonProperty(ApiPropertyNames.PAYMENT_DUE_DATE)
        String paymentDueDate
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

