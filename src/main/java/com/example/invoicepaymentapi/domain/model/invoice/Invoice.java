package com.example.invoicepaymentapi.domain.model.invoice;

import com.example.invoicepaymentapi.domain.model.user.UserId;

import java.time.LocalDateTime;

/**
 * 請求書集約ルート
 */
public record Invoice(
        InvoiceId id,
        UserId userId,
        IssueDate issueDate,
        PaymentAmount paymentAmount,
        Fee fee,
        FeeRate feeRate,
        TaxAmount taxAmount,
        TaxRate taxRate,
        TotalAmount totalAmount,
        PaymentDueDate paymentDueDate,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    /**
     * 新規請求書を作成するファクトリメソッド
     * 支払金額から手数料・消費税・請求金額を自動計算する
     *
     * 計算ロジックは各値オブジェクトのファクトリメソッドに委譲:
     * - Fee.create(paymentAmount, feeRate)
     * - TaxAmount.create(fee, taxRate)
     * - TotalAmount.create(paymentAmount, fee, taxAmount)
     *
     * @param userId ユーザーID
     * @param issueDate 発行日
     * @param paymentAmount 支払金額
     * @param paymentDueDate 支払期日
     * @return 請求書エンティティ
     */
    public static Invoice create(
            UserId userId,
            IssueDate issueDate,
            PaymentAmount paymentAmount,
            PaymentDueDate paymentDueDate
    ) {
        // 手数料率と消費税率を固定値で設定
        FeeRate feeRate = FeeRate.fixed();
        TaxRate taxRate = TaxRate.fixed();

        // 手数料を計算（値オブジェクトのドメイン知識を使用）
        Fee fee = Fee.create(paymentAmount, feeRate);

        // 消費税を計算（値オブジェクトのドメイン知識を使用）
        TaxAmount taxAmount = TaxAmount.create(fee, taxRate);

        // 請求金額を計算（値オブジェクトのドメイン知識を使用）
        TotalAmount totalAmount = TotalAmount.create(paymentAmount, fee, taxAmount);

        LocalDateTime now = LocalDateTime.now();
        return new Invoice(
                null, // 新規作成時はIDは未設定
                userId,
                issueDate,
                paymentAmount,
                fee,
                feeRate,
                taxAmount,
                taxRate,
                totalAmount,
                paymentDueDate,
                now,
                now
        );
    }

    /**
     * 既存請求書を再構築するファクトリメソッド（リポジトリから取得時など）
     */
    public static Invoice reconstruct(
            InvoiceId id,
            UserId userId,
            IssueDate issueDate,
            PaymentAmount paymentAmount,
            Fee fee,
            FeeRate feeRate,
            TaxAmount taxAmount,
            TaxRate taxRate,
            TotalAmount totalAmount,
            PaymentDueDate paymentDueDate,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        return new Invoice(
                id,
                userId,
                issueDate,
                paymentAmount,
                fee,
                feeRate,
                taxAmount,
                taxRate,
                totalAmount,
                paymentDueDate,
                createdAt,
                updatedAt
        );
    }
}
