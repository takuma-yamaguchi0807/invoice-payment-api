# Invoice Payment API

## プロジェクト概要

法人向けの請求書支払い管理サービスです。  
ユーザーが未来の支払期日の請求書データを登録しておくと、期日に残高がなくとも自動的に銀行振り込みを行うことができ、現金の支出を最大一ヶ月遅らせることができます。

本プロジェクトは、このサービスのプロトタイプとして、以下の機能を提供する REST API です：

- ユーザー登録・ログイン機能
- 請求書データの登録・一覧取得機能

## 技術スタック

- **言語**: Java 17
- **フレームワーク**: Spring Boot 3.2.0
- **データベース**: PostgreSQL 16.3
- **認証**: JWT (JSON Web Token)
- **パスワードハッシュ**: Argon2
- **ビルドツール**: Gradle 8.5

## セットアップと起動方法

### 前提条件

- Java 17 以上
- Docker と Docker Compose
- Gradle（または Gradle Wrapper）

### 1. データベースの起動

```bash
# PostgreSQLコンテナを起動
docker-compose up -d

# コンテナの状態を確認
docker-compose ps
```

PostgreSQL コンテナ起動時に、`docker/init/01-init.sql`が自動実行され、テーブルとインデックスが作成されます。

### 2. 環境変数の設定（必須）

アプリケーションとデータベースの起動には環境変数の設定が**必須**です。  
プロジェクトルートに`.env`ファイルを作成して、環境変数を設定してください。

```bash
# 環境変数ファイルを作成
cp env.example.txt .env
# .envファイルを編集して実際の値を設定
```

`.env`ファイルはアプリケーション起動時に自動的に読み込まれます。  
**注意**: `.env`ファイルは Git にコミットされません（`.gitignore`に含まれています）。  
本番環境では、`JWT_SECRET`などの秘密情報を必ず強力な値に書き換えてください。

### 3. アプリケーションの起動

```bash
# Gradle Wrapperを使用してアプリケーションを起動
# Linux/Mac/Git Bashの場合
./gradlew bootRun

# Windows (PowerShell/CMD) の場合
gradlew.bat bootRun

# または、ビルドしてから起動
./gradlew build
java -jar build/libs/invoice-payment-api-1.0.0.jar
```

アプリケーションは `http://localhost:8080/api/v1` で起動します。

### 4. 動作確認

Postman や curl などのツールを使用して、以下のサンプルリクエストで動作確認できます。

**ユーザー登録（認証不要）**

- **メソッド**: `POST`
- **URL**: `http://localhost:8080/api/v1/users`
- **ヘッダー**: `Content-Type: application/json`
- **ボディ**:

```json
{
  "companyName": "株式会社サンプル",
  "name": "山田太郎",
  "email": "yamada@example.com",
  "password": "Password123!"
}
```

**期待されるレスポンス**: `201 Created`（レスポンスボディなし）

### 停止方法

```bash
# アプリケーションを停止（Ctrl+C）

# PostgreSQLコンテナを停止
docker-compose down

# データも含めて完全に削除する場合
docker-compose down -v
```

## プロジェクト構成

本プロジェクトは **DDD（Domain-Driven Design）** を基本とし、レイヤードアーキテクチャを採用しています。

```
com.example.invoicepaymentapi
├── domain/              # ドメイン層
│   ├── model/          # エンティティ、値オブジェクト
│   ├── repository/     # リポジトリインターフェース
│   ├── service/        # ドメインサービス
│   ├── exception/      # ドメイン例外
│   └── shared/         # 共有値オブジェクト
├── application/         # アプリケーション層
│   └── usecase/        # ユースケース
│       ├── auth/       # 認証ユースケース
│       ├── invoices/   # 請求書ユースケース
│       └── users/       # ユーザーユースケース
├── presentation/        # プレゼンテーション層
│   ├── web/            # RESTコントローラー、リクエスト/レスポンス
│   ├── security/       # 認証・認可実装
│   ├── exception/      # 例外ハンドラー
│   └── error/          # エラーレスポンス
├── infrastructure/      # インフラ層
│   ├── repository/    # リポジトリ実装
│   ├── entity/         # JPAエンティティ
│   └── config/         # 設定クラス
└── InvoicePaymentApiApplication.java
```

### レイヤーの責務

- **ドメイン層**: ビジネスロジックの中核。エンティティ、値オブジェクト、ドメインサービスを定義
- **アプリケーション層**: ユースケースの実装。ドメイン層を利用してアプリケーションの機能を実現
- **プレゼンテーション層**: HTTP リクエスト/レスポンスの処理。REST コントローラー、認証・認可、設定クラスを配置
- **インフラ層**: データベースアクセスなど、外部システムとの連携。リポジトリの実装を配置

## ドキュメント

- **[API 仕様書](docs/openapi.yml)**: OpenAPI 3.0 形式の API 仕様
- **[設計意図](docs/architect.md)**: 各 API の設計意図と決定事項
- **[開発ガイドライン](AGENTS.md)**: AI 駆動開発におけるガイドライン
