-- ユーザーテーブル

CREATE TABLE users (
    id SERIAL PRIMARY KEY, -- ユーザーID
    company_name VARCHAR(255) NOT NULL, -- 企業名
    name VARCHAR(255) NOT NULL, -- 氏名
    email VARCHAR(255) UNIQUE NOT NULL, -- メールアドレス
    password VARCHAR(255) NOT NULL, -- パスワード
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), -- 作成日時
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW() -- 更新日時
);

-- 請求書テーブル

CREATE TABLE invoices (
    id SERIAL PRIMARY KEY, -- 請求書ID
    user_id INT REFERENCES users(id) ON DELETE CASCADE, -- 企業ID
    issue_date DATE NOT NULL, -- 発行日
    payment_amount DECIMAL(15, 2) NOT NULL, -- 支払金額
    fee DECIMAL(15, 2) NOT NULL, -- 手数料
    fee_rate DECIMAL(5, 2) NOT NULL, -- 手数料率
    tax_amount DECIMAL(15, 2) NOT NULL, -- 消費税
    tax_rate DECIMAL(5, 2) NOT NULL, -- 消費税率
    total_amount DECIMAL(15, 2) NOT NULL, -- 請求金額
    payment_due_date DATE NOT NULL, -- 支払期日
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), -- 作成日時
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW() -- 更新日時
);

-- インデックス作成（パフォーマンス最適化）
CREATE INDEX idx_invoices_user_payment_due ON invoices(user_id, payment_due_date);

