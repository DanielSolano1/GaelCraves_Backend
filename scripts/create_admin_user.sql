-- ============================================
-- Create Admin User Script for GaelCraves
-- ============================================
-- This script creates an admin user account
-- Password: Admin@123
-- Email: admin@gaelcraves.com
-- ============================================

-- Step 1: Ensure ADMIN role exists
INSERT INTO roles (role_name) 
VALUES ('ADMIN')
ON CONFLICT (role_name) DO NOTHING;

-- Step 2: Create admin user (password is BCrypt hashed "Admin@123")
INSERT INTO users (email, password, first_name, last_name, security_question, security_answer, created_at)
VALUES (
    'admin@gaelcraves.com',
    '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cyhQQT0Y/0zXJxmhNdJy5ZXpP4n4i',
    'Admin',
    'User',
    'What is your role?',
    '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cyhQQT0Y/0zXJxmhNdJy5ZXpP4n4i',
    CURRENT_TIMESTAMP
)
ON CONFLICT (email) DO NOTHING;

-- Step 3: Assign ADMIN role to admin user
INSERT INTO user_roles (user_id, role_id)
SELECT 
    u.user_id, 
    r.role_id
FROM users u
CROSS JOIN roles r
WHERE u.email = 'admin@gaelcraves.com'
  AND r.role_name = 'ADMIN'
ON CONFLICT (user_id, role_id) DO NOTHING;

-- Step 4: Also assign USER role for basic permissions
INSERT INTO user_roles (user_id, role_id)
SELECT 
    u.user_id, 
    r.role_id
FROM users u
CROSS JOIN roles r
WHERE u.email = 'admin@gaelcraves.com'
  AND r.role_name = 'USER'
ON CONFLICT (user_id, role_id) DO NOTHING;

-- Step 5: Verify the admin user was created successfully
SELECT 
    u.user_id,
    u.email,
    u.first_name,
    u.last_name,
    STRING_AGG(r.role_name, ', ') as roles
FROM users u
LEFT JOIN user_roles ur ON u.user_id = ur.user_id
LEFT JOIN roles r ON ur.role_id = r.role_id
WHERE u.email = 'admin@gaelcraves.com'
GROUP BY u.user_id, u.email, u.first_name, u.last_name;

-- ============================================
-- CREDENTIALS:
-- Email: admin@gaelcraves.com
-- Password: Admin@123
-- ============================================
