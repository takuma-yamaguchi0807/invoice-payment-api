package com.example.invoicepaymentapi.presentation.web.invoices;

import com.example.invoicepaymentapi.application.usecase.invoices.ListInvoicesUseCase;
import com.example.invoicepaymentapi.application.usecase.invoices.dto.InvoiceListResponseDto;
import com.example.invoicepaymentapi.application.usecase.invoices.dto.InvoiceListResponseDto.Item;
import com.example.invoicepaymentapi.application.usecase.invoices.dto.InvoiceListResponseDto.Pagination;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 請求書一覧取得APIのテスト
 */
@WebMvcTest(InvoiceController.class)
class ListInvoicesApiTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    ListInvoicesUseCase listInvoicesUseCase;

    @Nested
    @DisplayName("正常系")
    class NormalCase {
        @Test
        @DisplayName("クエリパラメータなしで請求書一覧取得が成功する")
        @WithMockUser(username = "1")
        void shouldListInvoicesWithNoQueryParameters() throws Exception {
            // Given
            InvoiceListResponseDto responseDto = new InvoiceListResponseDto(
                    List.of(),
                    new Pagination(1, 20, 0, 0)
            );
            when(listInvoicesUseCase.execute(eq(1), isNull(), isNull(), isNull(), isNull()))
                    .thenReturn(responseDto);

            // When & Then
            mockMvc.perform(get("/api/v1/invoices"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.items").isArray())
                    .andExpect(jsonPath("$.pagination").exists())
                    .andExpect(jsonPath("$.pagination.page_number").value(1))
                    .andExpect(jsonPath("$.pagination.page_size").value(20));
        }

        @Test
        @DisplayName("すべてのクエリパラメータを含めたリクエストで請求書一覧取得が成功する")
        @WithMockUser(username = "1")
        void shouldListInvoicesWithAllQueryParameters() throws Exception {
            // Given
            LocalDate from = LocalDate.now();
            LocalDate to = LocalDate.now().plusDays(30);
            InvoiceListResponseDto responseDto = new InvoiceListResponseDto(
                    List.of(
                            new Item(1, LocalDate.now(), LocalDate.now().plusDays(30), new BigDecimal("10000.00"))
                    ),
                    new Pagination(1, 10, 1, 1)
            );
            when(listInvoicesUseCase.execute(eq(1), eq(from.toString()), eq(to.toString()), eq(1), eq(10)))
                    .thenReturn(responseDto);

            // When & Then
            mockMvc.perform(get("/api/v1/invoices")
                            .param("paymentDueFrom", from.toString())
                            .param("paymentDueTo", to.toString())
                            .param("page_number", "1")
                            .param("page_size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.items").isArray())
                    .andExpect(jsonPath("$.items[0].id").value(1))
                    .andExpect(jsonPath("$.pagination.page_number").value(1))
                    .andExpect(jsonPath("$.pagination.page_size").value(10))
                    .andExpect(jsonPath("$.pagination.total").value(1))
                    .andExpect(jsonPath("$.pagination.total_pages").value(1));
        }
    }

    @Nested
    @DisplayName("異常系")
    class AbnormalCase {
        @Test
        @DisplayName("認証されていない場合、401 Unauthorizedを返す")
        void shouldReturn401WhenUnauthenticated() throws Exception {
            // When & Then
            mockMvc.perform(get("/api/v1/invoices"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("日付形式が不正な場合、400 Bad Requestを返す")
        @WithMockUser(username = "1")
        void shouldReturn400WhenDateFormatIsInvalid() throws Exception {
            // Given
            when(listInvoicesUseCase.execute(eq(1), eq("invalid-date"), any(), any(), any()))
                    .thenThrow(new com.example.invoicepaymentapi.domain.exception.DomainValidationException(
                            List.of(new com.example.invoicepaymentapi.domain.exception.ValidationError(
                                    "paymentDueFrom", "validation.date.format"
                            ))
                    ));

            // When & Then
            mockMvc.perform(get("/api/v1/invoices")
                            .param("paymentDueFrom", "invalid-date"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                    .andExpect(jsonPath("$.details.paymentDueFrom").exists());
        }

        @Test
        @DisplayName("ページ番号が0以下の場合、400 Bad Requestを返す")
        @WithMockUser(username = "1")
        void shouldReturn400WhenPageNumberIsZeroOrNegative() throws Exception {
            // Given
            when(listInvoicesUseCase.execute(eq(1), any(), any(), eq(0), any()))
                    .thenThrow(new com.example.invoicepaymentapi.domain.exception.DomainValidationException(
                            List.of(new com.example.invoicepaymentapi.domain.exception.ValidationError(
                                    "page_number", "validation.pageNumber.positive"
                            ))
                    ));

            // When & Then
            mockMvc.perform(get("/api/v1/invoices")
                            .param("page_number", "0"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                    .andExpect(jsonPath("$.details.page_number").exists());
        }

        @Test
        @DisplayName("支払期日の終了日が開始日より前の場合、400 Bad Requestを返す")
        @WithMockUser(username = "1")
        void shouldReturn400WhenPaymentDueToIsBeforePaymentDueFrom() throws Exception {
            // Given
            LocalDate from = LocalDate.now().plusDays(30);
            LocalDate to = LocalDate.now();
            when(listInvoicesUseCase.execute(eq(1), eq(from.toString()), eq(to.toString()), any(), any()))
                    .thenThrow(new com.example.invoicepaymentapi.domain.exception.DomainValidationException(
                            List.of(new com.example.invoicepaymentapi.domain.exception.ValidationError(
                                    "paymentDueTo", "validation.paymentDueTo.afterFrom"
                            ))
                    ));

            // When & Then
            mockMvc.perform(get("/api/v1/invoices")
                            .param("paymentDueFrom", from.toString())
                            .param("paymentDueTo", to.toString()))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                    .andExpect(jsonPath("$.details.paymentDueTo").exists());
        }
    }
}
