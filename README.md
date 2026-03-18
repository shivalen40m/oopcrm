# Dealership Management System (DMS)

A Java Swing desktop application for managing a car dealership, featuring role-based access control, full CRUD operations, and a PostgreSQL backend.

## Features

- Role-based access control (Admin, Sales, Service)
- Secure login with SHA-256 password hashing
- Dashboard with real-time statistics
- Vehicle, Customer, Sales, and Service management
- Admin-only user management
- Atomic sales transactions with automatic vehicle status updates
- Soft delete for customers

## Tech Stack

- Java (Swing GUI)
- PostgreSQL
- JDBC (`postgresql-42.7.10.jar`)

## Prerequisites

- Java JDK 11+
- PostgreSQL 12+

## Setup

### 1. Database

Create the database and run the schema:

```bash
psql -U postgres -c "CREATE DATABASE dealership_db;"
psql -U postgres -d dealership_db -f database_setup.sql
```

### 2. Configuration

Copy the example config and update your credentials:

```bash
cp config.properties.example config.properties
```

```properties
db.url=jdbc:postgresql://localhost:5432/dealership_db
db.user=postgres
db.password=your_password
```

Or use a `.env` file:

```bash
cp .env.example .env
```

### 3. Compile

**macOS/Linux:**
```bash
mkdir -p bin
javac -d bin -cp "lib/postgresql-42.7.10.jar" src/com/dms/**/*.java src/com/dms/*.java
```

**Windows (Command Prompt):**
```cmd
mkdir bin
javac -d bin -cp "lib\postgresql-42.7.10.jar" src\com\dms\**\*.java src\com\dms\*.java
```

> Windows CMD does not support `**` glob. Use PowerShell or list files explicitly:

**Windows (PowerShell):**
```powershell
mkdir bin
$files = Get-ChildItem -Recurse -Filter *.java src | Select-Object -ExpandProperty FullName
javac -d bin -cp "lib\postgresql-42.7.10.jar" $files
```

### 4. Run

**macOS/Linux:**
```bash
java -cp "bin:lib/postgresql-42.7.10.jar" com.dms.Main
```

**Windows:**
```cmd
java -cp "bin;lib\postgresql-42.7.10.jar" com.dms.Main
```

## Default Credentials

| Username   | Password    | Role    |
|------------|-------------|---------|
| `admin`    | `admin123`  | ADMIN   |
| `sales1`   | `admin123`  | SALES   |
| `service1` | `admin123`  | SERVICE |

## Project Structure

```
oopcrm/
├── src/com/dms/
│   ├── Main.java
│   ├── model/          # Customer, Vehicle, Sale, Service, User
│   ├── dao/            # CRUD operations for each model
│   ├── ui/             # Swing panels and frames
│   └── util/           # Utils, ConfigLoader, EnvLoader
├── lib/
│   └── postgresql-42.7.10.jar
├── database_setup.sql  # Schema + sample data
├── config.properties.example
└── .env.example
```

## User Roles

| Feature            | ADMIN | SALES | SERVICE |
|--------------------|:-----:|:-----:|:-------:|
| Dashboard          | ✅    | ✅    | ✅      |
| Vehicles           | ✅    | ✅    | ✅      |
| Customers          | ✅    | ✅    | ✅      |
| Sales              | ✅    | ✅    | ❌      |
| Service            | ✅    | ❌    | ✅      |
| User Management    | ✅    | ❌    | ❌      |

## Database Schema

- `users` — accounts with roles and hashed passwords
- `vehicles` — inventory with VIN, make, model, year, price, status
- `customers` — customer info with soft delete
- `sales` — transactions linking vehicle, customer, and employee
- `services` — service records per vehicle
