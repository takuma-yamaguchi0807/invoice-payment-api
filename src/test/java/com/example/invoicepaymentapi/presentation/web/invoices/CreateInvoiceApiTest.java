package com.example.invoicepaymentapi.presentation.web.invoices;

import com.example.invoicepaymentapi.application.usecase.invoices.CreateInvoiceUseCase;
import com.example.invoicepaymentapi.application.usecase.invoices.dto.CreateInvoiceResponseDto;
import com.example.invoicepaymentapi.domain.exception.DomainValidationException;
import com.example.invoicepaymentapi.domain.exception.ValidationError;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 請求書登録APIのテスト
 */
@WebMvcTest(InvoiceController.class)
class CreateInvoiceApiTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    CreateInvoiceUseCase createInvoiceUseCase;

    @Nested
    @DisplayName("正常系")
    class NormalCase {
        @Test
        @DisplayName("必須項目のみのリクエストで請求書登録が成功する")
        @WithMockUser(username = "1")
        void shouldCreateInvoiceWithRequiredFieldsOnly() throws Exception {
            // Given
            CreateInvoiceRequest request = new CreateInvoiceRequest(
                    LocalDate.now(),
                    new BigDecimal("10000.00"),
                    LocalDate.now().plusDays(30)
            );
            CreateInvoiceResponseDto responseDto = new CreateInvoiceResponseDto(1);
            when(createInvoiceUseCase.execute(eq(1), any())).thenReturn(responseDto);

            // When & Then
            mockMvc.perform(post("/api/v1/invoices")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1));
        }

        @Test
        @DisplayName("すべての項目を含めたリクエストで請求書登録が成功する")
        @WithMockUser(username = "1")
        void shouldCreateInvoiceWithAllFields() throws Exception {
            // Given
            CreateInvoiceRequest request = new CreateInvoiceRequest(
                    LocalDate.now(),
                    new BigDecimal("10000.00"),
                    LocalDate.now().plusDays(30)
            );
            CreateInvoiceResponseDto responseDto = new CreateInvoiceResponseDto(1);
            when(createInvoiceUseCase.execute(eq(1), any())).thenReturn(responseDto);

            // When & Then
            mockMvc.perform(post("/api/v1/invoices")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1));
        }
    }

    @Nested
    @DisplayName("異常系")
    class AbnormalCase {
        @Test
        @DisplayName("認証されていない場合、401 Unauthorizedを返す")
        void shouldReturn401WhenUnauthenticated() throws Exception {
            // Given
            CreateInvoiceRequest request = new CreateInvoiceRequest(
                    LocalDate.now(),
                    new BigDecimal("10000.00"),
                    LocalDate.now().plusDays(30)
            );

            // When & Then
            mockMvc.perform(post("/api/v1/invoices")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("必須項目が不足している場合、400 Bad Requestを返す")
        @WithMockUser(username = "1")
        void shouldReturn400WhenRequiredFieldsAreMissing() throws Exception {
            // Given - issueDateが不足
            String requestJson = """
                    {
                        "paymentAmount": 10000.00,
                        "paymentDueDate": "2024-02-15"
                    }
                    """;

            // When & Then
            mockMvc.perform(post("/api/v1/invoices")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("発行日が未来の場合、400 Bad Requestを返す")
        @WithMockUser(username = "1")
        void shouldReturn400WhenIssueDateIsFuture() throws Exception {
            // Given
            CreateInvoiceRequest request = new CreateInvoiceRequest(
                    LocalDate.now().plusDays(1),
                    new BigDecimal("10000.00"),
                    LocalDate.now().plusDays(30)
            );
            when(createInvoiceUseCase.execute(eq(1), any()))
                    .thenThrow(new DomainValidationException(List.of(
                            new ValidationError("issueDate", "validation.issueDate.future")
                    )));

            // When & Then
            mockMvc.perform(post("/api/v1/invoices")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                    .andExpect(jsonPath("$.details.issueDate").exists());
        }

        @Test
        @DisplayName("支払期日が過去または今日の場合、400 Bad Requestを返す")
        @WithMockUser(username = "1")
        void shouldReturn400WhenPaymentDueDateIsNotFuture() throws Exception {
            // Given
            CreateInvoiceRequest request = new CreateInvoiceRequest(
                    LocalDate.now(),
                    new BigDecimal("10000.00"),
                    LocalDate.now()
            );
            when(createInvoiceUseCase.execute(eq(1), any()))
                    .thenThrow(new DomainValidationException(List.of(
                            new ValidationError("paymentDueDate", "validation.paymentDueDate.notFuture")
                    )));

            // When & Then
            mockMvc.perform(post("/api/v1/invoices")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                    .andExpect(jsonPath("$.details.paymentDueDate").exists());
        }

        @Test
        @DisplayName("支払金額が0以下の場合、400 Bad Requestを返す")
        @WithMockUser(username = "1")
        void shouldReturn400WhenPaymentAmountIsZeroOrNegative() throws Exception {
            // Given
            CreateInvoiceRequest request = new CreateInvoiceRequest(
                    LocalDate.now(),
                    new BigDecimal("0"),
                    LocalDate.now().plusDays(30)
            );
            when(createInvoiceUseCase.execute(eq(1), any()))
                    .thenThrow(new DomainValidationException(List.of(
                            new ValidationError("paymentAmount", "validation.paymentAmount.positive")
                    )));

            // When & Then
            mockMvc.perform(post("/api/v1/invoices")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                    .andExpect(jsonPath("$.details.paymentAmount").exists());
        }
    }
}
