-- DDL script for current Genesis schema
-- Source of truth: JPA entities under src/main/java/com/breaze/genesis/entity
-- MySQL 8+

SET NAMES utf8mb4;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    full_name VARCHAR(150) NOT NULL,
    email VARCHAR(120) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    active TINYINT(1) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_users_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS plans (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255) NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_plans_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS plan_versions (
    id BIGINT NOT NULL AUTO_INCREMENT,
    plan_id BIGINT NOT NULL,
    token_limit INT NOT NULL,
    valid_from DATETIME(6) NOT NULL,
    valid_to DATETIME(6) NULL,
    created_at DATETIME(6) NOT NULL,
    duration_seconds INT NOT NULL,
    PRIMARY KEY (id),
    KEY idx_plan_versions_plan_id (plan_id),
    CONSTRAINT fk_plan_versions_plan
        FOREIGN KEY (plan_id) REFERENCES plans (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS operation_catalog (
    id BIGINT NOT NULL AUTO_INCREMENT,
    code VARCHAR(20) NOT NULL,
    name VARCHAR(150) NOT NULL,
    description VARCHAR(255) NULL,
    base_cost INT NOT NULL,
    active TINYINT(1) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_operation_catalog_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS exchange_rates (
    id BIGINT NOT NULL AUTO_INCREMENT,
    cop_per_usd DOUBLE NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS token_wallet (
    user_id BIGINT NOT NULL,
    tokens_available INT NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    version INT NOT NULL,
    PRIMARY KEY (user_id),
    CONSTRAINT fk_token_wallet_user
        FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS subscriptions (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    plan_version_id BIGINT NOT NULL,
    start_date DATETIME(6) NOT NULL,
    end_date DATETIME(6) NULL,
    status VARCHAR(20) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    KEY idx_subscriptions_user_id (user_id),
    KEY idx_subscriptions_plan_version_id (plan_version_id),
    CONSTRAINT fk_subscriptions_user
        FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_subscriptions_plan_version
        FOREIGN KEY (plan_version_id) REFERENCES plan_versions (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS operation_executions (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    operation_catalog_id BIGINT NULL,
    base_cost INT NOT NULL,
    total_tokens_consumed INT NOT NULL,
    status VARCHAR(20) NOT NULL,
    executed_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    KEY idx_operation_executions_user_id (user_id),
    KEY idx_operation_executions_operation_catalog_id (operation_catalog_id),
    CONSTRAINT fk_operation_executions_user
        FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_operation_executions_operation_catalog
        FOREIGN KEY (operation_catalog_id) REFERENCES operation_catalog (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS token_transactions (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    reference_type VARCHAR(50) NOT NULL,
    reference_id BIGINT NULL,
    amount INT NOT NULL,
    remaining_amount INT NULL,
    expires_at DATETIME(6) NULL,
    created_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    KEY idx_token_transactions_user_id (user_id),
    CONSTRAINT fk_token_transactions_user
        FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
