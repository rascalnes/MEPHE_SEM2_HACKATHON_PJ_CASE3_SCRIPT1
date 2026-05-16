-- Insert test admin user (password: admin123)
-- Password is hashed with bcrypt (will be implemented in stage 3)
INSERT INTO users (id, login, password, role) VALUES
    ('11111111-1111-1111-1111-111111111111', 'admin', '2Ot+HxCQqDStuvSyXz9lec6iC0ecF1GTnXcsOMRS9jFEjQGYngwpUa9k4OGr7wED', 'ADMIN')
    ON CONFLICT (login) DO NOTHING;

-- Insert test regular user (password: user123)
INSERT INTO users (id, login, password, role) VALUES
    ('22222222-2222-2222-2222-222222222222', 'user1', 'p8HUgLKqwCDzdxzByGtjGDS6QQhx6k7FQuZAvinogPsDUQr1asnwUs/Zx5zG/Ijf', 'USER')
    ON CONFLICT (login) DO NOTHING;

-- Insert test draft draw
INSERT INTO draws (name, status, created_by) VALUES
    ('New Year Lottery 2025', 'DRAFT', '11111111-1111-1111-1111-111111111111')
    ON CONFLICT DO NOTHING;