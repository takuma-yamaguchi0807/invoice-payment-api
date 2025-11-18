package com.example.invoicepaymentapi.presentation.web.invoices;

import com.example.invoicepaymentapi.application.usecase.invoices.dto.CreateInvoiceResponseDto;
import com.example.invoicepaymentapi.presentation.web.constants.ApiPropertyNames;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 請求書登録レスポンス
 */
public record CreateInvoiceResponse(
        @JsonProperty(ApiPropertyNames.ID)
        Integer id
) {
    /**
     * application層のDTOからResponseを作成
     *
     * @param dto CreateInvoiceResponseDto
     * @return CreateInvoiceResponse
     */
    public static CreateInvoiceResponse from(CreateInvoiceResponseDto dto) {
        return new CreateInvoiceResponse(dto.id());
    }
}

