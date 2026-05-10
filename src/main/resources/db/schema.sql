-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Users table
CREATE TABLE IF NOT EXISTS users (
                                     id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    login VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) DEFAULT 'USER' CHECK (role IN ('ADMIN', 'USER')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

-- Draws table
CREATE TABLE IF NOT EXISTS draws (
                                     id BIGSERIAL PRIMARY KEY,
                                     name VARCHAR(100) NOT NULL,
    status VARCHAR(20) NOT NULL CHECK (status IN ('DRAFT', 'ACTIVE', 'FINISHED')),
    created_by UUID REFERENCES users(id) ON DELETE SET NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    started_at TIMESTAMP,
    finished_at TIMESTAMP
    );

-- Draw results table
CREATE TABLE IF NOT EXISTS draw_results (
                                            id BIGSERIAL PRIMARY KEY,
                                            draw_id BIGINT NOT NULL REFERENCES draws(id) ON DELETE CASCADE,
    winning_combo VARCHAR(255) NOT NULL,
    generated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

-- Tickets table
CREATE TABLE IF NOT EXISTS tickets (
                                       id BIGSERIAL PRIMARY KEY,
                                       draw_id BIGINT NOT NULL REFERENCES draws(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    numbers VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'WIN', 'LOSE')),
    checked_at TIMESTAMP,
    purchased_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

-- Indexes for performance
CREATE INDEX idx_draws_status ON draws(status);
CREATE INDEX idx_tickets_user_id ON tickets(user_id);
CREATE INDEX idx_tickets_draw_id ON tickets(draw_id);
CREATE INDEX idx_tickets_status ON tickets(status);
CREATE INDEX idx_draw_results_draw_id ON draw_results(draw_id);
CREATE INDEX idx_users_login ON users(login);

-- Comments
COMMENT ON TABLE users IS 'System users (admins and regular users)';
COMMENT ON TABLE draws IS 'Lottery draws';
COMMENT ON TABLE draw_results IS 'Results of completed draws';
COMMENT ON TABLE tickets IS 'Lottery tickets purchased by users';