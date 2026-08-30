INSERT INTO users (
    email,
    password_hash,
    first_name,
    last_name,
    about,
    github_username,
    is_active,
    created_at,
    updated_at,
    average_rating,
    reviews_count,
    completed_projects_count,
    completion_rate,
    system_role_id
)
SELECT
    'admin@teamforge.example',
    '$2b$10$NhrB.I83/Mg/YxeYH9EhTO3aO2oS9ZFTALhLuFcOGfsCQ9zv.F6je',
    'System',
    'Administrator',
    '',
    '',
    true,
    now(),
    now(),
    0,
    0,
    0,
    0,
    (SELECT id FROM system_roles WHERE name = 'ADMIN')
WHERE NOT EXISTS (
    SELECT 1 FROM users WHERE email = 'admin@teamforge.example'
);
