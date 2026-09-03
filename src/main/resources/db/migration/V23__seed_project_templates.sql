INSERT INTO skills (name, type, category_id)
SELECT 'Java', 'GLOBAL', c.id
FROM skill_categories c
WHERE c.name = 'PROGRAMMING'
  AND NOT EXISTS (
      SELECT 1 FROM skills s WHERE s.name = 'Java' AND s.category_id = c.id
  );

INSERT INTO skills (name, type, category_id)
SELECT 'SQL', 'GLOBAL', c.id
FROM skill_categories c
WHERE c.name = 'PROGRAMMING'
  AND NOT EXISTS (
      SELECT 1 FROM skills s WHERE s.name = 'SQL' AND s.category_id = c.id
  );

INSERT INTO skills (name, type, category_id)
SELECT 'React', 'GLOBAL', c.id
FROM skill_categories c
WHERE c.name = 'PROGRAMMING'
  AND NOT EXISTS (
      SELECT 1 FROM skills s WHERE s.name = 'React' AND s.category_id = c.id
  );

INSERT INTO skills (name, type, category_id)
SELECT 'UI/UX Design', 'GLOBAL', c.id
FROM skill_categories c
WHERE c.name = 'DESIGN'
  AND NOT EXISTS (
      SELECT 1 FROM skills s WHERE s.name = 'UI/UX Design' AND s.category_id = c.id
  );

INSERT INTO skills (name, type, category_id)
SELECT 'Team Leadership', 'GLOBAL', c.id
FROM skill_categories c
WHERE c.name = 'MANAGEMENT'
  AND NOT EXISTS (
      SELECT 1 FROM skills s WHERE s.name = 'Team Leadership' AND s.category_id = c.id
  );


INSERT INTO project_templates (name, description, is_active, created_at)
VALUES ('Web Application', 'Standard template for a full-stack web application project', true, now());

INSERT INTO project_templates (name, description, is_active, created_at)
VALUES ('Legacy Mobile App', 'Retired template kept for reference; not offered for new projects', false, now());


INSERT INTO template_roles (role_name, description, required_count, template_id)
SELECT 'Backend Developer', 'Implements server-side business logic and APIs', 2, t.id
FROM project_templates t
WHERE t.name = 'Web Application';

INSERT INTO template_roles (role_name, description, required_count, template_id)
SELECT 'Frontend Developer', 'Implements the client-side UI', 2, t.id
FROM project_templates t
WHERE t.name = 'Web Application';

INSERT INTO template_roles (role_name, description, required_count, template_id)
SELECT 'Project Manager', 'Coordinates the project team and timeline', 1, t.id
FROM project_templates t
WHERE t.name = 'Web Application';


INSERT INTO template_role_skills (min_level, template_role_id, skill_id)
SELECT 3, tr.id, s.id
FROM template_roles tr
JOIN project_templates t ON t.id = tr.template_id AND t.name = 'Web Application'
JOIN skill_categories c ON c.name = 'PROGRAMMING'
JOIN skills s ON s.name = 'Java' AND s.category_id = c.id
WHERE tr.role_name = 'Backend Developer';

INSERT INTO template_role_skills (min_level, template_role_id, skill_id)
SELECT 2, tr.id, s.id
FROM template_roles tr
JOIN project_templates t ON t.id = tr.template_id AND t.name = 'Web Application'
JOIN skill_categories c ON c.name = 'PROGRAMMING'
JOIN skills s ON s.name = 'SQL' AND s.category_id = c.id
WHERE tr.role_name = 'Backend Developer';

INSERT INTO template_role_skills (min_level, template_role_id, skill_id)
SELECT 3, tr.id, s.id
FROM template_roles tr
JOIN project_templates t ON t.id = tr.template_id AND t.name = 'Web Application'
JOIN skill_categories c ON c.name = 'PROGRAMMING'
JOIN skills s ON s.name = 'React' AND s.category_id = c.id
WHERE tr.role_name = 'Frontend Developer';

INSERT INTO template_role_skills (min_level, template_role_id, skill_id)
SELECT 2, tr.id, s.id
FROM template_roles tr
JOIN project_templates t ON t.id = tr.template_id AND t.name = 'Web Application'
JOIN skill_categories c ON c.name = 'DESIGN'
JOIN skills s ON s.name = 'UI/UX Design' AND s.category_id = c.id
WHERE tr.role_name = 'Frontend Developer';

INSERT INTO template_role_skills (min_level, template_role_id, skill_id)
SELECT 3, tr.id, s.id
FROM template_roles tr
JOIN project_templates t ON t.id = tr.template_id AND t.name = 'Web Application'
JOIN skill_categories c ON c.name = 'MANAGEMENT'
JOIN skills s ON s.name = 'Team Leadership' AND s.category_id = c.id
WHERE tr.role_name = 'Project Manager';
