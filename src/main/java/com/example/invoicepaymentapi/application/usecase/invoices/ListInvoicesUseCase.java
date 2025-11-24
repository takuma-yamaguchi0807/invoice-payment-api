package com.example.invoicepaymentapi.application.usecase.invoices;

import com.example.invoicepaymentapi.application.usecase.invoices.dto.InvoiceListResponseDto;
import com.example.invoicepaymentapi.domain.model.invoice.PaymentDueDateRange;
import com.example.invoicepaymentapi.domain.model.user.UserId;
import com.example.invoicepaymentapi.domain.repository.InvoiceRepository;
import com.example.invoicepaymentapi.domain.shared.pagination.Pagination;
import lombok.RequiredArgsConstructor;
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
     * @param userId ユーザーID
     * @param paymentDueFrom 支払期日の開始日（nullの場合は明日をデフォルト）
     * @param paymentDueTo 支払期日の終了日（nullの場合は1ヶ月後をデフォルト）
     * @param pageNumber ページ番号（nullの場合は1をデフォルト）
     * @param pageSize ページサイズ（nullの場合は20をデフォルト）
     * @return 請求書一覧レスポンスDTO
     * @throws com.example.invoicepaymentapi.domain.exception.DomainValidationException バリデーションエラーがある場合
     */
    @Transactional(readOnly = true)
    public InvoiceListResponseDto execute(
            Integer userId,
            String paymentDueFrom,
            String paymentDueTo,
            Integer pageNumber,
            Integer pageSize
    ) {
        // 支払期日範囲の作成（デフォルト値計算・バリデーション・値オブジェクト生成を一括処理）
        PaymentDueDateRange dateRange = PaymentDueDateRange.create(paymentDueFrom, paymentDueTo);

        // ページネーションパラメータの値オブジェクト作成（nullの場合はデフォルト値を使用）
        // ofCreateOrDefaultメソッドがデフォルト値適用とバリデーションを一括処理する
        UserId userIdVo = UserId.create(userId);
        PageNumber pageNumberVo = PageNumber.ofCreateOrDefault(pageNumber);
        PageSize pageSizeVo = PageSize.ofCreateOrDefault(pageSize);
        Pagination pagination = new Pagination(pageNumberVo, pageSizeVo);

        // 請求書一覧を取得
        List<Invoice> invoices = invoiceRepository
                .findByUserIdAndPaymentDueDateBetween(
                        userIdVo,
                        dateRange.from(),
                        dateRange.to(),
                        pagination.calculateOffset(),
                        pagination.getLimit()
                );

        // 総件数を取得
        long total = invoiceRepository.countByUserIdAndPaymentDueDateBetween(
                userIdVo,
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

