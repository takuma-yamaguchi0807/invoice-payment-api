package com.example.invoicepaymentapi.domain.repository;

import com.example.invoicepaymentapi.domain.model.invoice.Invoice;
import com.example.invoicepaymentapi.domain.model.invoice.InvoiceId;

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
}

