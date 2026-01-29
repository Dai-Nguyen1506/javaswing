-- Database: OOP Java

DROP DATABASE IF EXISTS "OOP Java";

CREATE DATABASE "OOP Java"
    WITH
    OWNER = postgres
    ENCODING = 'UTF8'
    LC_COLLATE = 'English_United States.1252'
    LC_CTYPE = 'English_United States.1252'
    LOCALE_PROVIDER = 'libc'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1
    IS_TEMPLATE = False;
	
-- DROP TABLES 
DROP TABLE IF EXISTS rental_contract CASCADE;
DROP TABLE IF EXISTS lesson CASCADE;
DROP TABLE IF EXISTS instructor CASCADE;
DROP TABLE IF EXISTS items CASCADE;
DROP TABLE IF EXISTS equipment CASCADE;
DROP TABLE IF EXISTS customer CASCADE;
DROP TABLE IF EXISTS log_history CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- DROP TYPES
DROP TYPE IF EXISTS user_role_enum CASCADE;
DROP TYPE IF EXISTS log_status_enum CASCADE;
DROP TYPE IF EXISTS item_condition_enum CASCADE;
DROP TYPE IF EXISTS item_status_enum CASCADE;
DROP TYPE IF EXISTS rental_status_enum CASCADE;

-- CREATE ENUM TYPES
CREATE TYPE user_role_enum AS ENUM ('Admin', 'Staff');
CREATE TYPE log_status_enum AS ENUM ('login', 'logout');
CREATE TYPE item_condition_enum AS ENUM ('Good', 'Needs Repair', 'Out of Service');
CREATE TYPE item_status_enum AS ENUM ('Available', 'Unavailable');
CREATE TYPE rental_status_enum AS ENUM ('Draft', 'Active', 'Returned', 'Completed', 'Overdue');

-- USERS
CREATE TABLE users (
    user_id SERIAL PRIMARY KEY,
    user_name VARCHAR(100) NOT NULL UNIQUE,
    user_password VARCHAR(255) NOT NULL,
    user_role user_role_enum DEFAULT 'Staff',
    user_email VARCHAR(100) UNIQUE,
    user_phone_number VARCHAR(20)
);

-- LOG HISTORY
CREATE TABLE log_history (
    log_id SERIAL PRIMARY KEY,
    log_status log_status_enum DEFAULT 'login',
    log_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    user_id INT NOT NULL,
    CONSTRAINT fk_log_user
        FOREIGN KEY (user_id)
        REFERENCES users(user_id)
        ON DELETE CASCADE ON UPDATE CASCADE
);

-- CUSTOMER
CREATE TABLE customer (
    customer_id SERIAL PRIMARY KEY,
    cus_name VARCHAR(100) NOT NULL,
    cus_email VARCHAR(100) UNIQUE,
    cus_phone_number VARCHAR(20)
);

-- EQUIPMENT
CREATE TABLE equipment (
    equipment_id SERIAL PRIMARY KEY,
    equipment_name VARCHAR(100) NOT NULL,
    equipment_fee NUMERIC(10,2) NOT NULL DEFAULT 40,
    equipment_number INT NOT NULL CHECK (equipment_number >= 0),
	equipment_available INT NOT NULL CHECK (equipment_available >=0)
);

-- ITEMS
CREATE TABLE items (
    item_id SERIAL PRIMARY KEY,
    item_name VARCHAR(100) NOT NULL,
    item_condition item_condition_enum DEFAULT 'Good',
	item_status item_status_enum DEFAULT 'Available',
    equipment_id INT NOT NULL,
    CONSTRAINT fk_items_equipment
        FOREIGN KEY (equipment_id)
        REFERENCES equipment(equipment_id)
        ON DELETE RESTRICT ON UPDATE CASCADE
);

-- INSTRUCTOR
CREATE TABLE instructor (
    instructor_id SERIAL PRIMARY KEY,
    instructor_name VARCHAR(100) NOT NULL,
    instructor_email VARCHAR(100) UNIQUE,
    instructor_phone_number VARCHAR(20)
);

-- LESSON
CREATE TABLE lesson (
    lesson_id SERIAL PRIMARY KEY,
    lesson_name VARCHAR(100) NOT NULL,
    lesson_fee NUMERIC(10,2) CHECK (lesson_fee >= 0),
    instructor_id INT,
    CONSTRAINT fk_lesson_instructor
        FOREIGN KEY (instructor_id)
        REFERENCES instructor(instructor_id)
        ON DELETE SET NULL ON UPDATE CASCADE
);

-- RENTAL CONTRACT
CREATE TABLE rental_contract (
    rental_id SERIAL PRIMARY KEY,
    rental_duration INT NOT NULL CHECK (rental_duration > 0),
    rental_fee NUMERIC(10,2),
    rental_status rental_status_enum DEFAULT 'Active',

    lesson_id INT,
    customer_id INT NOT NULL,
    equipment_id INT NOT NULL,

    CONSTRAINT fk_rental_lesson
        FOREIGN KEY (lesson_id)
        REFERENCES lesson(lesson_id)
        ON DELETE SET NULL ON UPDATE CASCADE,

    CONSTRAINT fk_rental_customer
        FOREIGN KEY (customer_id)
        REFERENCES customer(customer_id)
        ON DELETE RESTRICT ON UPDATE CASCADE,

    CONSTRAINT fk_rental_equipment
        FOREIGN KEY (equipment_id)
        REFERENCES equipment(equipment_id)
        ON DELETE RESTRICT ON UPDATE CASCADE
);

-- INDEXES
CREATE INDEX idx_log_history_user_id ON log_history(user_id);
CREATE INDEX idx_items_equipment_id ON items(equipment_id);
CREATE INDEX idx_rental_customer_id ON rental_contract(customer_id);
CREATE INDEX idx_rental_equipment_id ON rental_contract(equipment_id);

