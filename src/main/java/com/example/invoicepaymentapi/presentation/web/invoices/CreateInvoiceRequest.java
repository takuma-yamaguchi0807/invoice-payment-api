package com.example.invoicepaymentapi.presentation.web.invoices;

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
}

