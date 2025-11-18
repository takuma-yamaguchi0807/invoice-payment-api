package com.example.invoicepaymentapi.domain.shared.pagination;

import com.example.invoicepaymentapi.domain.exception.DomainValidationException;
import com.example.invoicepaymentapi.domain.service.DomainValidationService;

/**
 * ページネーション集約ルート
 * ページ番号とページサイズを組み合わせた集約
 */
public record Pagination(
        PageNumber pageNumber,
        PageSize pageSize
) {
    /**
     * デフォルト値でページネーションを作成
     *
     * @return デフォルト値（pageNumber: 1, pageSize: 20）のページネーション
     */
    public static Pagination defaultValue() {
        return new Pagination(
                PageNumber.defaultValue(),
                PageSize.defaultValue()
        );
    }

    /**
     * ページ番号とページサイズからページネーションを作成
     * nullの場合はデフォルト値を使用
     *
     * @param pageNumberValue ページ番号（nullの場合はデフォルト値1を使用）
     * @param pageSizeValue ページサイズ（nullの場合はデフォルト値20を使用）
     * @return ページネーション
     */
    public static Pagination ofCreateOrDefault(Integer pageNumberValue, Integer pageSizeValue) {
        PageNumber pageNumber = PageNumber.ofCreateOrDefault(pageNumberValue);
        PageSize pageSize = PageSize.ofCreateOrDefault(pageSizeValue);
        return new Pagination(pageNumber, pageSize);
    }

    /**
     * ページ番号とページサイズからページネーションを作成
     * バリデーションを実施し、エラーがあれば例外をスロー
     *
     * @param pageNumberValue ページ番号
     * @param pageSizeValue ページサイズ
     * @return ページネーション
     * @throws DomainValidationException バリデーションエラーがある場合
     */
    public static Pagination create(Integer pageNumberValue, Integer pageSizeValue) {
        // 全フィールドのバリデーションを一括で実行
        DomainValidationService.validateAll(
                () -> PageNumber.validate(pageNumberValue),
                () -> PageSize.validate(pageSizeValue)
        );

        // バリデーション成功後、値オブジェクトを作成
        PageNumber pageNumber = PageNumber.create(pageNumberValue);
        PageSize pageSize = PageSize.create(pageSizeValue);

        return new Pagination(pageNumber, pageSize);
    }

    /**
     * 既存データからページネーションを再構築
     *
     * @param pageNumberValue ページ番号
     * @param pageSizeValue ページサイズ
     * @return ページネーション
     */
    public static Pagination reconstruct(Integer pageNumberValue, Integer pageSizeValue) {
        return new Pagination(
                PageNumber.reconstruct(pageNumberValue),
                PageSize.reconstruct(pageSizeValue)
        );
    }

    /**
     * OFFSET値を計算
     * SQLのOFFSET句で使用する値を返す
     *
     * @return OFFSET値（(pageNumber - 1) * pageSize）
     */
    public int calculateOffset() {
        return (pageNumber.value() - 1) * pageSize.value();
    }

    /**
     * LIMIT値を取得
     * SQLのLIMIT句で使用する値を返す
     *
     * @return LIMIT値（pageSize）
     */
    public int getLimit() {
        return pageSize.value();
    }
}

