package com.example.invoicepaymentapi.domain.repository;

import com.example.invoicepaymentapi.domain.model.invoice.Invoice;
import com.example.invoicepaymentapi.domain.model.invoice.InvoiceId;
import com.example.invoicepaymentapi.domain.model.invoice.PaymentDueDate;
import com.example.invoicepaymentapi.domain.model.user.UserId;

import java.util.List;

/**
 * 請求書リポジトリインターフェース
 * ドメイン層の集約ルートを扱う
 */
public interface InvoiceRepository {
    /**
     * 請求書を保存
     *
     * @param invoice 保存する請求書
     * @return 保存された請求書のID
     */
    InvoiceId save(Invoice invoice);

    /**
     * ユーザーIDと支払期日の期間で請求書を検索（ページネーション対応）
     *
     * @param userId ユーザーID
     * @param paymentDueFrom 支払期日の開始日（含む）
     * @param paymentDueTo 支払期日の終了日（含む）
     * @param offset オフセット
     * @param limit 取得件数
     * @return 請求書のリスト（支払期日昇順、同じ支払期日の場合は発行日昇順）
     */
    List<Invoice> findByUserIdAndPaymentDueDateBetween(
            UserId userId,
            PaymentDueDate paymentDueFrom,
            PaymentDueDate paymentDueTo,
            int offset,
            int limit
    );

    /**
     * ユーザーIDと支払期日の期間で請求書の件数を取得
     *
     * @param userId ユーザーID
     * @param paymentDueFrom 支払期日の開始日（含む）
     * @param paymentDueTo 支払期日の終了日（含む）
     * @return 請求書の件数
     */
    long countByUserIdAndPaymentDueDateBetween(
            UserId userId,
            PaymentDueDate paymentDueFrom,
            PaymentDueDate paymentDueTo
    );
}

