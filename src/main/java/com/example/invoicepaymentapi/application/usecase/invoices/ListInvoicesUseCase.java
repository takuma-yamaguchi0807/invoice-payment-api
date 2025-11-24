package com.example.invoicepaymentapi.application.usecase.invoices;

import com.example.invoicepaymentapi.application.usecase.invoices.dto.InvoiceListResponseDto;
import com.example.invoicepaymentapi.domain.model.invoice.PaymentDueDate;
import com.example.invoicepaymentapi.domain.model.invoice.PaymentDueDateRange;
import com.example.invoicepaymentapi.domain.model.user.UserId;
import com.example.invoicepaymentapi.domain.repository.InvoiceRepository;
import com.example.invoicepaymentapi.domain.service.DomainValidationService;
import com.example.invoicepaymentapi.domain.shared.pagination.Pagination;
import com.example.invoicepaymentapi.presentation.web.constants.ApiPropertyNames;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.invoicepaymentapi.domain.shared.pagination.PageNumber;
import com.example.invoicepaymentapi.domain.shared.pagination.PageSize;
import com.example.invoicepaymentapi.domain.model.invoice.Invoice;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 請求書一覧取得ユースケース
 */
@Service
@RequiredArgsConstructor
public class ListInvoicesUseCase {
    private final InvoiceRepository invoiceRepository;

    /**
     * 請求書一覧を取得する
     *
     * @param userId ユーザーIDの値オブジェクト（JWTから取得、バリデーション済み）
     * @param paymentDueFrom 支払期日の開始日（nullの場合は明日をデフォルト）
     * @param paymentDueTo 支払期日の終了日（nullの場合は1ヶ月後をデフォルト）
     * @param pageNumber ページ番号（nullの場合は1をデフォルト）
     * @param pageSize ページサイズ（nullの場合は20をデフォルト）
     * @return 請求書一覧レスポンスDTO
     * @throws com.example.invoicepaymentapi.domain.exception.DomainValidationException バリデーションエラーがある場合
     */
    @Transactional(readOnly = true)
    public InvoiceListResponseDto execute(
            UserId userId,
            String paymentDueFrom,
            String paymentDueTo,
            Integer pageNumber,
            Integer pageSize
    ) {
        // 全バリデーションを一括で実行（複数フィールドのエラーを同時に返すため）
        DomainValidationService.validateAll(
                // 開始日のバリデーション（null/空の場合はデフォルト値を使用するため、バリデーションはスキップ）
                () -> StringUtils.isEmpty(paymentDueFrom) ? List.of() : PaymentDueDateRange.validateDate(paymentDueFrom, ApiPropertyNames.PAYMENT_DUE_FROM),
                // 終了日のバリデーション（null/空の場合はデフォルト値を使用するため、バリデーションはスキップ）
                () -> StringUtils.isEmpty(paymentDueTo) ? List.of() : PaymentDueDateRange.validateDate(paymentDueTo, ApiPropertyNames.PAYMENT_DUE_TO),
                // ページ番号のバリデーション（nullの場合はデフォルト値を使用するため、バリデーションはスキップ）
                () -> pageNumber == null ? List.of() : PageNumber.validate(pageNumber),
                // ページサイズのバリデーション（nullの場合はデフォルト値を使用するため、バリデーションはスキップ）
                () -> pageSize == null ? List.of() : PageSize.validate(pageSize)
        );

        // バリデーション通過後、値オブジェクトを作成（デフォルト値適用）
        PaymentDueDate from = PaymentDueDate.ofCreateOrDefaultFrom(paymentDueFrom);
        PaymentDueDate to = PaymentDueDate.ofCreateOrDefaultTo(paymentDueTo, from);

        // 相関チェック（from <= to）を含む支払期日範囲の作成
        PaymentDueDateRange dateRange = PaymentDueDateRange.create(from, to);

        PageNumber pageNumberVo = PageNumber.ofCreateOrDefault(pageNumber);
        PageSize pageSizeVo = PageSize.ofCreateOrDefault(pageSize);
        Pagination pagination = new Pagination(pageNumberVo, pageSizeVo);

        // 請求書一覧を取得
        List<Invoice> invoices = invoiceRepository
                .findByUserIdAndPaymentDueDateBetween(
                        userId,
                        dateRange.from(),
                        dateRange.to(),
                        pagination.calculateOffset(),
                        pagination.getLimit()
                );

        // 総件数を取得
        long total = invoiceRepository.countByUserIdAndPaymentDueDateBetween(
                userId,
                dateRange.from(),
                dateRange.to()
                );

        // 総ページ数を計算
        int totalPages = (int) Math.ceil((double) total / pagination.pageSize().value());

        // DTOに変換
        List<InvoiceListResponseDto.Item> items = invoices.stream()
                .map(InvoiceListResponseDto.Item::from)
                .collect(Collectors.toList());

        InvoiceListResponseDto.Pagination paginationDto = new InvoiceListResponseDto.Pagination(
                pagination.pageNumber().value(),
                pagination.pageSize().value(),
                (int) total,
                totalPages
        );

        return new InvoiceListResponseDto(items, paginationDto);
    }
}

