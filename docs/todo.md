# TODO リスト

このドキュメントは、実装済みタスクと未実装タスクをチェックリスト形式で管理します。

## 実装済み

### ユーザー登録機能

- [x] ユーザー登録 API（POST /api/v1/users）
- [x] RegisterUserUseCase 実装
- [x] UserController 実装
- [x] UserRepository 実装（UserRepositoryImpl）
- [x] UserEntity 実装
- [x] 値オブジェクトの validate()メソッド実装（CompanyName, UserName, Email, Password）
- [x] バリデーションエラーの一括返却対応
- [x] メールアドレス重複チェック
- [x] パスワードハッシュ化（Argon2）

### 請求書登録機能

- [x] 請求書登録 API（POST /api/v1/invoices）
- [x] CreateInvoiceUseCase 実装
- [x] InvoiceController 実装（暫定：X-User-Id ヘッダーでユーザー ID 取得）
- [x] InvoiceRepository 実装（InvoiceRepositoryImpl）
- [x] InvoiceEntity 実装
- [x] 値オブジェクトの validate()メソッド実装（IssueDate, PaymentAmount, PaymentDueDate, TaxRate, FeeRate, Fee, TaxAmount, TotalAmount）
- [x] バリデーションエラーの一括返却対応
- [ ] 手数料・消費税・請求金額の自動計算

### インフラストラクチャ

- [x] JPA エンティティ実装（UserEntity, InvoiceEntity）
- [x] リポジトリ実装（EntityManager 使用）
- [ ] データベース接続設定（application.yml）

### ドメインモデル

- [x] HashedPassword.verify()メソッド実装
- [x] 値オブジェクトの validate()メソッド実装

## 未実装

### ログイン機能

- [ ] ログイン API（POST /api/v1/auth/login）
- [ ] LoginUseCase 実装
- [ ] LoginController 実装
- [ ] JWT トークン発行機能
- [ ] AccessToken 値オブジェクトの実装（JWT 生成・検証）

### 請求書一覧取得機能

- [ ] 請求書一覧取得 API（GET /api/v1/invoices）
- [ ] ListInvoicesUseCase 実装
- [ ] ページネーション実装
- [ ] 期間フィルタリング実装（paymentDueFrom, paymentDueTo）
- [ ] ソート実装（支払期日昇順、発行日昇順）

### 認証・認可

- [ ] Spring Security 設定
- [ ] JWT 認証フィルター実装
- [ ] 認証エラーハンドリング（401 Unauthorized）
- [ ] InvoiceController の JWT 認証対応（X-User-Id ヘッダーから JWT 認証に変更）

### 改善・修正

- [ ] UserId の validate()メソッド追加（一貫性のため）
- [ ] README のエンドポイント URL 更新（/api → /api/v1）
- [ ] OpenAPI 仕様書のエンドポイント URL 更新（/api → /api/v1）
