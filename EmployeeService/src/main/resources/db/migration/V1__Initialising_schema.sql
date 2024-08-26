CREATE TABLE department (
    id SERIAL PRIMARY KEY,
    created_by VARCHAR(255),
    created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(255),
    updated_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(50) DEFAULT 'ACTIVE',
    name VARCHAR(255) NOT NULL,
    description TEXT
);

CREATE TABLE employee (
     id SERIAL PRIMARY KEY,
     created_by VARCHAR(255),
     created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
     updated_by VARCHAR(255),
     updated_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
     status VARCHAR(50) DEFAULT 'ACTIVE',
     first_name VARCHAR(255) NOT NULL,
     last_name VARCHAR(255) NOT NULL,
     middle_name VARCHAR(255),
     user_id BIGINT,
     email VARCHAR(255) UNIQUE NOT NULL,
     phone_number VARCHAR(20),
     hire_date DATE,
     employment_status VARCHAR(100),
     position VARCHAR(100),
     salary DECIMAL(19, 4),
     department_id INTEGER,
     CONSTRAINT fk_department FOREIGN KEY (department_id)
         REFERENCES department(id)
 );
