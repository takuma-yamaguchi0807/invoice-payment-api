package com.example.invoicepaymentapi.application.usecase.invoices.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 請求書登録リクエストDTO
 * presentation層から受け取り、domain層の値オブジェクトに変換するためのDTO
 */
public record CreateInvoiceRequestDto(
        LocalDate issueDate,
        BigDecimal paymentAmount,
        LocalDate paymentDueDate
) {
}

