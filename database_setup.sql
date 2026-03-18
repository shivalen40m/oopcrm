-- Dealership Management System Database Setup
-- For PostgreSQL Query Tool (pgAdmin/DBeaver)

-- STEP 1: Create database (run this first on postgres database)
-- DROP DATABASE IF EXISTS dealership_db;
-- CREATE DATABASE dealership_db;

-- STEP 2: Connect to dealership_db database in your GUI tool
-- STEP 3: Run the script below

-- Users table
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    active BOOLEAN DEFAULT true
);

-- Customers table with soft delete
CREATE TABLE customers (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    phone VARCHAR(20),
    address TEXT,
    active BOOLEAN DEFAULT true
);

-- Vehicles table
CREATE TABLE vehicles (
    id SERIAL PRIMARY KEY,
    vin VARCHAR(17) UNIQUE NOT NULL,
    make VARCHAR(50) NOT NULL,
    model VARCHAR(50) NOT NULL,
    year INTEGER NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    status VARCHAR(20) DEFAULT 'Available'
);

-- Sales table
CREATE TABLE sales (
    id SERIAL PRIMARY KEY,
    vehicle_id INTEGER REFERENCES vehicles(id),
    customer_id INTEGER REFERENCES customers(id),
    employee_id INTEGER REFERENCES users(id),
    sale_date DATE DEFAULT CURRENT_DATE,
    sale_price DECIMAL(10, 2) NOT NULL,
    payment_method VARCHAR(50) NOT NULL
);

-- Services table
CREATE TABLE services (
    id SERIAL PRIMARY KEY,
    vehicle_id INTEGER REFERENCES vehicles(id),
    customer_id INTEGER REFERENCES customers(id),
    description TEXT NOT NULL,
    cost DECIMAL(10, 2) NOT NULL,
    service_date DATE DEFAULT CURRENT_DATE
);

-- Insert default users (password: admin123 hashed with SHA-256)
INSERT INTO users (username, password, role, full_name, active) VALUES
('admin', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'ADMIN', 'Admin User', true),
('sales1', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'SALES', 'Sales Person', true),
('service1', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'SERVICE', 'Service Tech', true);

-- Sample customers
INSERT INTO customers (name, email, phone, address, active) VALUES
('John Doe', 'john@email.com', '555-0101', '123 Main St', true),
('Jane Smith', 'jane@email.com', '555-0102', '456 Oak Ave', true),
('Bob Johnson', 'bob@email.com', '555-0103', '789 Elm St', true);

-- Sample vehicles
INSERT INTO vehicles (vin, make, model, year, price, status) VALUES
('1HGBH41JXMN109186', 'Honda', 'Accord', 2023, 28500.00, 'Available'),
('1FTFW1ET5DFC10312', 'Ford', 'F-150', 2022, 45000.00, 'Available'),
('5YJSA1E14HF123456', 'Tesla', 'Model S', 2024, 89990.00, 'Available');
