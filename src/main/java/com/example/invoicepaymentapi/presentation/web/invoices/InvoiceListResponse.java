package com.example.invoicepaymentapi.presentation.web.invoices;

import com.example.invoicepaymentapi.application.usecase.invoices.dto.InvoiceListResponseDto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 請求書一覧取得レスポンス
 * プロトタイプ＋一覧なので、詳細情報はノイズ。期限と金額だけわかればいい。
 */
public record InvoiceListResponse(
        List<Item> items,
        Pagination pagination
) {
    /**
     * application層のDTOからResponseを作成
     *
     * @param dto InvoiceListResponseDto
     * @return InvoiceListResponse
     */
    public static InvoiceListResponse from(InvoiceListResponseDto dto) {
        List<Item> items = dto.items().stream()
                .map(item -> new Item(
                        item.id(),
                        item.issueDate(),
                        item.paymentDueDate(),
                        item.totalAmount()
                ))
                .collect(Collectors.toList());

        Pagination pagination = new Pagination(
                dto.pagination().page_number(),
                dto.pagination().page_size(),
                dto.pagination().total(),
                dto.pagination().total_pages()
        );

        return new InvoiceListResponse(items, pagination);
    }

    /**
     * 請求書一覧項目
     * プロトタイプ段階では、期限と金額、発行日を返す
     */
    public record Item(
            Integer id,
            LocalDate issueDate,
            LocalDate paymentDueDate,
            BigDecimal totalAmount
    ) {
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

