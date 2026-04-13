-- Seed script for Genesis entities
-- MySQL 8+
-- Inserts are ordered to respect FK dependencies defined in JPA entities.

START TRANSACTION;

-- USERS (Role: USER, ADMIN)
-- BCrypt hash corresponds to raw password: password
INSERT INTO users (id, full_name, email, password, role, active, created_at, updated_at)
VALUES
    (1, 'Genesis Admin', 'admin@genesis.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ADMIN', 1, '2026-04-13 09:00:00', NULL),
    (2, 'Genesis User', 'user1@genesis.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'USER', 1, '2026-04-13 09:05:00', NULL)
ON DUPLICATE KEY UPDATE
    full_name = VALUES(full_name),
    email = VALUES(email),
    password = VALUES(password),
    role = VALUES(role),
    active = VALUES(active),
    updated_at = VALUES(updated_at);

-- PLANS
INSERT INTO plans (id, name, description, created_at, updated_at)
VALUES
    (1, 'Free', 'Base free plan', '2026-04-13 09:10:00', NULL),
    (2, 'Pro', 'Professional plan', '2026-04-13 09:10:00', NULL),
    (3, 'Enterprise', 'Enterprise plan', '2026-04-13 09:10:00', NULL)
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    description = VALUES(description),
    updated_at = VALUES(updated_at);

-- PLAN VERSIONS (FK: plan_versions.plan_id -> plans.id)
INSERT INTO plan_versions (id, plan_id, token_limit, valid_from, valid_to, created_at, duration_seconds)
VALUES
    (1, 1, 200,  '2026-04-13 09:15:00', NULL, '2026-04-13 09:15:00', 2592000),
    (2, 2, 1000, '2026-04-13 09:15:00', NULL, '2026-04-13 09:15:00', 2592000),
    (3, 3, 5000, '2026-04-13 09:15:00', NULL, '2026-04-13 09:15:00', 2592000)
ON DUPLICATE KEY UPDATE
    plan_id = VALUES(plan_id),
    token_limit = VALUES(token_limit),
    valid_from = VALUES(valid_from),
    valid_to = VALUES(valid_to),
    duration_seconds = VALUES(duration_seconds);

-- OPERATION CATALOG
INSERT INTO operation_catalog (id, code, name, description, base_cost, active, created_at, updated_at)
VALUES
    (1, 'OP-01', 'Loan Cost Estimator', 'Builds an amortization plan and monthly payments.', 50, 1, '2026-04-13 09:20:00', NULL),
    (2, 'OP-02', 'COP USD Converter', 'Converts amounts between COP and USD.', 20, 1, '2026-04-13 09:20:00', NULL),
    (3, 'OP-03', 'BMI Calculator', 'Calculates body mass index and category.', 15, 1, '2026-04-13 09:20:00', NULL),
    (4, 'OP-04', 'Sleep Cycle Calculator', 'Computes suggested sleep/wake windows.', 20, 1, '2026-04-13 09:20:00', NULL)
ON DUPLICATE KEY UPDATE
    code = VALUES(code),
    name = VALUES(name),
    description = VALUES(description),
    base_cost = VALUES(base_cost),
    active = VALUES(active),
    updated_at = VALUES(updated_at);

-- EXCHANGE RATE
INSERT INTO exchange_rates (id, cop_per_usd, updated_at)
VALUES
    (1, 4000.00, '2026-04-13 09:25:00')
ON DUPLICATE KEY UPDATE
    cop_per_usd = VALUES(cop_per_usd),
    updated_at = VALUES(updated_at);
COMMIT;
