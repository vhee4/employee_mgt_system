-- Seed the permissions table
INSERT INTO permissions (name, description, created_by, created_time, status) VALUES
    ('READ_USERS', 'Can read all users', 'System', CURRENT_TIMESTAMP, 'ACTIVE'),
    ('READ_USER', 'Can read a user', 'System', CURRENT_TIMESTAMP, 'ACTIVE'),
    ('EDIT_USER', 'Can update user details', 'System', CURRENT_TIMESTAMP, 'ACTIVE'),
    ('DELETE_USER', 'Can delete a user', 'System', CURRENT_TIMESTAMP, 'ACTIVE'),
    ('CREATE_USER', 'Can create a user', 'System', CURRENT_TIMESTAMP, 'ACTIVE'),
    ('READ_EMPLOYEES', 'Can read all employees', 'System', CURRENT_TIMESTAMP, 'ACTIVE'),
    ('READ_EMPLOYEE', 'Can read an employee', 'System', CURRENT_TIMESTAMP, 'ACTIVE'),
    ('CREATE_EMPLOYEE', 'Can create an employee', 'System', CURRENT_TIMESTAMP, 'ACTIVE'),
    ('READ_EMPLOYEE_BY_DEPT', 'Can read all employees by department', 'System', CURRENT_TIMESTAMP, 'ACTIVE'),
    ('EDIT_EMPLOYEE', 'Can update employee details', 'System', CURRENT_TIMESTAMP, 'ACTIVE'),
    ('DELETE_EMPLOYEE', 'Can delete an employee', 'System', CURRENT_TIMESTAMP, 'ACTIVE'),
    ('INVITE_USERS', 'Can invite users', 'System', CURRENT_TIMESTAMP, 'ACTIVE'),
    ('CREATE_DEPT', 'Can create department', 'System', CURRENT_TIMESTAMP, 'ACTIVE'),
    ('EDIT_DEPT', 'Can update department details', 'System', CURRENT_TIMESTAMP, 'ACTIVE'),
    ('READ_DEPT', 'Can read a department', 'System', CURRENT_TIMESTAMP, 'ACTIVE'),
    ('READ_DEPTS', 'Can read departments', 'System', CURRENT_TIMESTAMP, 'ACTIVE');

-- Seed the roles table and assign permissions
INSERT INTO roles (name, description, created_by, created_time, status) VALUES
    ('ADMIN', 'Role for admin', 'System', CURRENT_TIMESTAMP, 'ACTIVE'),
    ('MANAGER', 'Role for manager', 'System', CURRENT_TIMESTAMP, 'ACTIVE'),
    ('EMPLOYEE', 'Role for employee', 'System', CURRENT_TIMESTAMP, 'ACTIVE');

-- Assign permissions to roles
INSERT INTO role_permissions (role_id, permission_id) VALUES
    ((SELECT id FROM roles WHERE name = 'ADMIN'), (SELECT id FROM permissions WHERE name = 'CREATE_DEPT')),
    ((SELECT id FROM roles WHERE name = 'ADMIN'), (SELECT id FROM permissions WHERE name = 'EDIT_DEPT')),
    ((SELECT id FROM roles WHERE name = 'ADMIN'), (SELECT id FROM permissions WHERE name = 'READ_DEPT')),
    ((SELECT id FROM roles WHERE name = 'ADMIN'), (SELECT id FROM permissions WHERE name = 'READ_DEPTS')),
    ((SELECT id FROM roles WHERE name = 'ADMIN'), (SELECT id FROM permissions WHERE name = 'INVITE_USERS')),
    ((SELECT id FROM roles WHERE name = 'ADMIN'), (SELECT id FROM permissions WHERE name = 'DELETE_EMPLOYEE')),
    ((SELECT id FROM roles WHERE name = 'ADMIN'), (SELECT id FROM permissions WHERE name = 'DELETE_USER')),
    ((SELECT id FROM roles WHERE name = 'ADMIN'), (SELECT id FROM permissions WHERE name = 'EDIT_EMPLOYEE')),
    ((SELECT id FROM roles WHERE name = 'ADMIN'), (SELECT id FROM permissions WHERE name = 'CREATE_EMPLOYEE')),
    ((SELECT id FROM roles WHERE name = 'ADMIN'), (SELECT id FROM permissions WHERE name = 'READ_EMPLOYEE')),
    ((SELECT id FROM roles WHERE name = 'ADMIN'), (SELECT id FROM permissions WHERE name = 'CREATE_USER')),
    ((SELECT id FROM roles WHERE name = 'ADMIN'), (SELECT id FROM permissions WHERE name = 'READ_EMPLOYEES')),
    ((SELECT id FROM roles WHERE name = 'ADMIN'), (SELECT id FROM permissions WHERE name = 'EDIT_USER')),
    ((SELECT id FROM roles WHERE name = 'ADMIN'), (SELECT id FROM permissions WHERE name = 'READ_USERS')),
    ((SELECT id FROM roles WHERE name = 'ADMIN'), (SELECT id FROM permissions WHERE name = 'READ_USER')),

    ((SELECT id FROM roles WHERE name = 'MANAGER'), (SELECT id FROM permissions WHERE name = 'READ_EMPLOYEE_BY_DEPT')),
    ((SELECT id FROM roles WHERE name = 'MANAGER'), (SELECT id FROM permissions WHERE name = 'READ_EMPLOYEE')),
    ((SELECT id FROM roles WHERE name = 'MANAGER'), (SELECT id FROM permissions WHERE name = 'READ_DEPT')),

    ((SELECT id FROM roles WHERE name = 'EMPLOYEE'), (SELECT id FROM permissions WHERE name = 'READ_EMPLOYEE'));

-- Seed the admin user
INSERT INTO users (first_name, last_name, email, password, username, created_by, created_time, status) VALUES
    ('Super', 'Admin', 'admin@gmail.com', '$2a$12$H8xedOvwpkLfuF071IMSIOmv3F1b43x8cO/FQ805xcepgpAWnsYZq', 'Super Admin', 'System', CURRENT_TIMESTAMP, 'ACTIVE');  -- Adjust the password hash as needed

-- Assign the admin role to the admin user
INSERT INTO user_roles (user_id, role_id) VALUES
    ((SELECT id FROM users WHERE email = 'admin@gmail.com'), (SELECT id FROM roles WHERE name = 'ADMIN'));
