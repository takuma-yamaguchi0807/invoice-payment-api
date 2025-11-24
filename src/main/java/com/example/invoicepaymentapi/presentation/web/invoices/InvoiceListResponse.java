package com.example.invoicepaymentapi.presentation.web.invoices;

import com.example.invoicepaymentapi.application.usecase.invoices.dto.InvoiceListResponseDto;
import com.example.invoicepaymentapi.presentation.web.constants.ApiPropertyNames;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 請求書一覧取得レスポンス
 * プロトタイプ＋一覧なので、詳細情報はノイズ。期限と金額だけわかればいい。
 */
public record InvoiceListResponse(
        @JsonProperty(ApiPropertyNames.ITEMS)
        List<Item> items,
        @JsonProperty(ApiPropertyNames.PAGINATION)
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
            @JsonProperty(ApiPropertyNames.ID)
            Integer id,
            @JsonProperty(ApiPropertyNames.ISSUE_DATE)
            LocalDate issueDate,
            @JsonProperty(ApiPropertyNames.PAYMENT_DUE_DATE)
            LocalDate paymentDueDate,
            @JsonProperty(ApiPropertyNames.TOTAL_AMOUNT)
            BigDecimal totalAmount
    ) {
    }

    /**
     * ページネーション情報
     */
    public record Pagination(
            @JsonProperty(ApiPropertyNames.PAGE_NUMBER)
            Integer page_number,
            @JsonProperty(ApiPropertyNames.PAGE_SIZE)
            Integer page_size,
            @JsonProperty(ApiPropertyNames.TOTAL)
            Integer total,
            @JsonProperty(ApiPropertyNames.TOTAL_PAGES)
            Integer total_pages
    ) {
    }
}

