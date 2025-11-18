package com.example.invoicepaymentapi.application.usecase.invoices;

import com.example.invoicepaymentapi.application.usecase.invoices.dto.CreateInvoiceRequestDto;
import com.example.invoicepaymentapi.application.usecase.invoices.dto.CreateInvoiceResponseDto;
import com.example.invoicepaymentapi.domain.model.invoice.*;
import com.example.invoicepaymentapi.domain.model.user.UserId;
import com.example.invoicepaymentapi.domain.repository.InvoiceRepository;
import com.example.invoicepaymentapi.domain.service.DomainValidationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 請求書登録ユースケース
 */
@Service
public class CreateInvoiceUseCase {
    private final InvoiceRepository invoiceRepository;

    public CreateInvoiceUseCase(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    /**
     * 請求書を登録する
     *
     * @param userId ユーザーID
     * @param requestDto 請求書登録リクエストDTO
     * @return 作成された請求書のID
     * @throws com.example.invoicepaymentapi.domain.exception.DomainValidationException バリデーションエラーがある場合（全フィールドのエラーを一括で返す）
     */
    @Transactional
    public CreateInvoiceResponseDto execute(Integer userId, CreateInvoiceRequestDto requestDto) {
        // 全フィールドのバリデーションを一括で実行
        DomainValidationService.validateAll(
            () -> IssueDate.validate(requestDto.issueDate().toString()),
            () -> PaymentAmount.validate(requestDto.paymentAmount()),
            () -> PaymentDueDate.validate(requestDto.paymentDueDate().toString())
        );

        // バリデーション成功後、値オブジェクトを作成
        UserId userIdVo = UserId.create(userId);
        IssueDate issueDate = IssueDate.create(requestDto.issueDate().toString());
        PaymentAmount paymentAmount = PaymentAmount.create(requestDto.paymentAmount());
        PaymentDueDate paymentDueDate = PaymentDueDate.create(requestDto.paymentDueDate().toString());

        // 請求書集約ルートを作成（手数料・消費税・請求金額は自動計算される）
        Invoice invoice = Invoice.create(userIdVo, issueDate, paymentAmount, paymentDueDate);

        // 請求書を保存
        InvoiceId invoiceId = invoiceRepository.save(invoice);

        // レスポンスDTOを作成
        return CreateInvoiceResponseDto.from(invoiceId);
    }
}

