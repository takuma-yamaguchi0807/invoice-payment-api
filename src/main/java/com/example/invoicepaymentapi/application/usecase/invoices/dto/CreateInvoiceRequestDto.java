package com.example.invoicepaymentapi.application.usecase.invoices.dto;

import java.math.BigDecimal;

/**
 * 請求書登録リクエストDTO
 * presentation層から受け取り、domain層の値オブジェクトに変換するためのDTO
 * 日付フィールドはStringとして扱い、Domain層の値オブジェクトでバリデーションと変換を行う
 */
public record CreateInvoiceRequestDto(
        String issueDate,
        BigDecimal paymentAmount,
        String paymentDueDate
) {
}

