package com.example.invoicepaymentapi.application.usecase.invoices;

import com.example.invoicepaymentapi.application.usecase.invoices.dto.CreateInvoiceRequestDto;
import com.example.invoicepaymentapi.application.usecase.invoices.dto.CreateInvoiceResponseDto;
import com.example.invoicepaymentapi.domain.exception.DomainValidationException;
import com.example.invoicepaymentapi.domain.exception.ValidationError;
import com.example.invoicepaymentapi.domain.model.invoice.*;
import com.example.invoicepaymentapi.domain.model.user.UserId;
import com.example.invoicepaymentapi.domain.repository.InvoiceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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
     * @throws DomainValidationException バリデーションエラーがある場合（全フィールドのエラーを一括で返す）
     */
    @Transactional
    public CreateInvoiceResponseDto execute(Integer userId, CreateInvoiceRequestDto requestDto) {
        // 全フィールドのバリデーションを一括で実行
        List<ValidationError> allErrors = new ArrayList<>();
        allErrors.addAll(IssueDate.validate(requestDto.issueDate()));
        allErrors.addAll(PaymentAmount.validate(requestDto.paymentAmount()));
        allErrors.addAll(PaymentDueDate.validate(requestDto.paymentDueDate()));

        // エラーがあれば一括で例外を投げる
        if (!allErrors.isEmpty()) {
            throw new DomainValidationException(allErrors);
        }

        // バリデーション成功後、値オブジェクトを作成
        UserId userIdVo = UserId.ofCreate(userId);
        IssueDate issueDate = IssueDate.ofCreate(requestDto.issueDate());
        PaymentAmount paymentAmount = PaymentAmount.ofCreate(requestDto.paymentAmount());
        PaymentDueDate paymentDueDate = PaymentDueDate.ofCreate(requestDto.paymentDueDate());

        // 請求書集約ルートを作成（手数料・消費税・請求金額は自動計算される）
        Invoice invoice = Invoice.ofCreate(userIdVo, issueDate, paymentAmount, paymentDueDate);

        // 請求書を保存
        Invoice savedInvoice = invoiceRepository.save(invoice);

        // レスポンスDTOを作成
        return CreateInvoiceResponseDto.from(savedInvoice);
    }
}

