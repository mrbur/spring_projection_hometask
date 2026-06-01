
DROP TABLE IF EXISTS employee;
DROP TABLE IF EXISTS department;


CREATE TABLE department (
id UUID PRIMARY KEY,
name VARCHAR(255) NOT NULL CHECK (length(trim(name)) > 0)
);


CREATE TABLE employee (
id UUID PRIMARY KEY,
first_name VARCHAR(255) NOT NULL CHECK (length(trim(first_name)) > 0),
last_name VARCHAR(255),
salary NUMERIC(15, 2) NOT NULL CHECK (salary > 0),
position VARCHAR(255) NOT NULL, -- Новая колонка для должности
department UUID NOT NULL,

CONSTRAINT fk_employee_department FOREIGN KEY (department) REFERENCES department(id)
);