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

### 2. 環境変数の設定（オプション）

プロジェクトルートに`.env`ファイルを作成して、環境変数を設定できます。

```bash
# 環境変数ファイルを作成（オプション）
cp env.example.txt .env
# .envファイルを編集して実際の値を設定
```

`.env`ファイルはアプリケーション起動時に自動的に読み込まれます。  
**注意**: `.env`ファイルは Git にコミットされません（`.gitignore`に含まれています）。  
本番環境では、`JWT_SECRET`などの秘密情報を必ず強力な値に書き換えてください。

### 3. アプリケーションの起動

```bash
# Gradle Wrapperを使用してアプリケーションを起動
./gradlew bootRun

# または、ビルドしてから起動
./gradlew build
java -jar build/libs/invoice-payment-api-1.0.0.jar
```

アプリケーションは `http://localhost:8080/api` で起動します。

### 4. 動作確認

```bash
# ヘルスチェック（アプリケーションが起動している場合）
curl http://localhost:8080/api
```

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
│   └── service/        # ドメインサービス
├── application/         # アプリケーション層
│   ├── usecase/        # ユースケース
│   ├── dto/            # データ転送オブジェクト
│   └── service/        # アプリケーションサービス
├── presentation/        # プレゼンテーション層
│   ├── web/            # RESTコントローラー
│   ├── security/       # 認証・認可実装
│   └── config/         # 設定クラス
├── infrastructure/      # インフラ層
│   └── persistence/    # データベース実装
└── InvoicePaymentApiApplication.java
```

### レイヤーの責務

- **ドメイン層**: ビジネスロジックの中核。エンティティ、値オブジェクト、ドメインサービスを定義
- **アプリケーション層**: ユースケースの実装。ドメイン層を利用してアプリケーションの機能を実現
- **プレゼンテーション層**: HTTP リクエスト/レスポンスの処理。REST コントローラー、認証・認可、設定クラスを配置
- **インフラ層**: データベースアクセスなど、外部システムとの連携。リポジトリの実装を配置

### 依存方向

外側の層は内側の層に依存し、逆方向の依存は禁止されています。  
これにより、ドメインロジックがインフラ層の変更に影響されない設計となっています。

## ドキュメント

- **[API 仕様書](docs/openapi.yml)**: OpenAPI 3.0 形式の API 仕様
- **[設計意図](docs/architect.md)**: 各 API の設計意図と決定事項
- **[データベーススキーマ](docs/db.md)**: データベーステーブル定義
- **[開発ガイドライン](AGENTS.md)**: AI 駆動開発におけるガイドライン
