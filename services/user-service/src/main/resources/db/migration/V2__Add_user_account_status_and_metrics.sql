-- Add account status, metrics, and badges support for admin user management
ALTER TABLE users ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE';
ALTER TABLE users ADD COLUMN eco_points BIGINT NOT NULL DEFAULT 0;
ALTER TABLE users ADD COLUMN kg_collected DOUBLE PRECISION NOT NULL DEFAULT 0;

CREATE TABLE IF NOT EXISTS user_badges (
    user_id BIGINT NOT NULL,
    badge VARCHAR(100) NOT NULL,
    PRIMARY KEY (user_id, badge),
    CONSTRAINT fk_user_badge_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_users_status ON users(status);
CREATE INDEX IF NOT EXISTS idx_users_role ON users(role);
