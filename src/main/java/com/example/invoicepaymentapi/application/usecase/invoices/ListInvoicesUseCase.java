package com.example.invoicepaymentapi.application.usecase.invoices;

import com.example.invoicepaymentapi.application.usecase.invoices.dto.InvoiceListResponseDto;
import com.example.invoicepaymentapi.domain.model.invoice.PaymentDueDate;
import com.example.invoicepaymentapi.domain.model.user.UserId;
import com.example.invoicepaymentapi.domain.repository.InvoiceRepository;
import com.example.invoicepaymentapi.domain.service.DomainValidationService;
import com.example.invoicepaymentapi.domain.shared.pagination.Pagination;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.invoicepaymentapi.domain.exception.ValidationError;
import com.example.invoicepaymentapi.domain.exception.DomainValidationException;
import com.example.invoicepaymentapi.domain.shared.pagination.PageNumber;
import com.example.invoicepaymentapi.domain.shared.pagination.PageSize;
import com.example.invoicepaymentapi.domain.model.invoice.Invoice;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 請求書一覧取得ユースケース
 */
@Service
public class ListInvoicesUseCase {
    private final InvoiceRepository invoiceRepository;

    public ListInvoicesUseCase(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    /**
     * 請求書一覧を取得する
     *
     * @param userId ユーザーID
     * @param paymentDueFrom 支払期日の開始日（nullの場合は今日をデフォルト）
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
        // デフォルト値の設定
        String from = paymentDueFrom != null ? paymentDueFrom : LocalDate.now().toString();
        String to;
        if (paymentDueTo != null) {
            to = paymentDueTo;
        } else {
            // paymentDueFromが指定されている場合はその日から1ヶ月後、未指定の場合は今日から1ヶ月後
            LocalDate fromDate = paymentDueFrom != null
                    ? LocalDate.parse(paymentDueFrom, DateTimeFormatter.ISO_LOCAL_DATE)
                    : LocalDate.now();
            to = fromDate.plusMonths(1).toString();
        }

        // 日付形式とページネーションのバリデーション
        DomainValidationService.validateAll(
                () -> PaymentDueDate.validate(from),
                () -> PaymentDueDate.validate(to),
                () -> PageNumber.validate(pageNumber),
                () -> PageSize.validate(pageSize)
        );

        // 日付範囲のバリデーション（from <= to）
        LocalDate fromDate = LocalDate.parse(from, DateTimeFormatter.ISO_LOCAL_DATE);
        LocalDate toDate = LocalDate.parse(to, DateTimeFormatter.ISO_LOCAL_DATE);
        List<ValidationError> errors = new ArrayList<>();
        if (fromDate.isAfter(toDate)) {
            errors.add(new ValidationError(
                    "paymentDueTo",
                    "validation.paymentDueTo.range"
            ));
        }

        if (!errors.isEmpty()) {
            throw new DomainValidationException(errors);
        }

        // 値オブジェクトの作成
        UserId userIdVo = UserId.create(userId);
        PaymentDueDate paymentDueFromVo = PaymentDueDate.create(from);
        PaymentDueDate paymentDueToVo = PaymentDueDate.create(to);
        Pagination pagination = Pagination.ofCreateOrDefault(pageNumber, pageSize);

        // 請求書一覧を取得
        List<Invoice> invoices = invoiceRepository
                .findByUserIdAndPaymentDueDateBetween(
                        userIdVo,
                        paymentDueFromVo,
                        paymentDueToVo,
                        pagination.calculateOffset(),
                        pagination.getLimit()
                );

        // 総件数を取得
        long total = invoiceRepository.countByUserIdAndPaymentDueDateBetween(
                userIdVo,
                paymentDueFromVo,
                paymentDueToVo
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

