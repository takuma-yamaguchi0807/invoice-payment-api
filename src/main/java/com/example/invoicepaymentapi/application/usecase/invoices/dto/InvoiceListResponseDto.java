package com.example.invoicepaymentapi.application.usecase.invoices.dto;

import com.example.invoicepaymentapi.domain.model.invoice.Invoice;

import java.util.List;

/**
 * 請求書一覧レスポンスDTO
 * domain層のInvoice集約ルートのリストから受け取り、presentation層に渡すためのDTO
 */
public record InvoiceListResponseDto(
        List<Item> items,
        Pagination pagination
) {
    /**
     * 請求書一覧項目
     */
    public record Item(
            Integer id,
            java.time.LocalDate issueDate,
            java.time.LocalDate paymentDueDate,
            java.math.BigDecimal totalAmount
    ) {
        /**
         * Invoice集約ルートからItemを作成
         *
         * @param invoice Invoice集約ルート
         * @return Item
         */
        public static Item from(Invoice invoice) {
            return new Item(
                    invoice.id() != null ? invoice.id().value() : null,
                    invoice.issueDate().value(),
                    invoice.paymentDueDate().value(),
                    invoice.totalAmount().value()
            );
        }
    }

    /**
     * ページネーション情報
     */
    public record Pagination(
            Integer page_number,
            Integer page_size,
            Integer total,
            Integer total_pages
    ) {
    }
}

