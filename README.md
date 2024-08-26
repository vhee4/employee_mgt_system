# Employee Management System

## Overview

This Employee Management System is a microservices-based application that provides CRUD operations for managing employees and departments, with role-based access control. The application uses JWT for authentication and is integrated with Postgres databases for persistence.

## Prerequisites

Before setting up the application, ensure you have the following installed:

- **Java 17**
- **Postgres 14 or Postgres 9** (Flyway migration tool supports these versions)
- **Git** (for version control)
- **Postman and Swagger** (for API testing)

## Initial Setup

### 1. **Configure JWT Keys**

- Both the Authentication Server and the Employee Service require keys for encoding and decoding JWT tokens.
- You need to generate these keys and store them locally.
- Update the file paths for these keys in the `application.properties` files for both services, which you can access through the Config Server.

### 2. **Database Configuration**

- The application uses Postgres databases. Each service (Auth Server and Employee Service) has its own database.
- Ensure that you configure the Postgres details in the `app.properties` files of each service. You can manage these properties centrally via the Config Server.
- If needed, update the Postgres configuration on GitHub, commit the changes, and the Config Server will pick them up.

### 3. **Migration Setup**

- Flyway is used for database migrations. Once the application is up and running, Flyway will automatically apply the migrations.
- Ensure that the appropriate migration folders are in place for each service.

## Running the Application

### Step-by-Step Guide

1. **Start the Services in the Following Order**:
   - **Discovery Service**: Service registry for microservices.
   - **Config Server**: Centralized configuration management.
   - **API Gateway**: Entry point for all client requests.It runs on port 8082
   - **Authentication Server**: Handles user authentication and token generation.
   - **Employee Service**: Manages employee and department data.

2. **Accessing the Services**:
   - You can access the Auth Server and Employee Service through the API Gateway using Postman.
   - Swagger is also available for API documentation and Api testing.

### Swagger URLs

- The Swagger documentation for each service can be accessed via the following URLs (replace `localhost` and ports with your specific configuration):
  - **Authentication Service**: `http://localhost:8080/swagger-ui/index.html#/`
  - **Employee Service**: `http://localhost:8081/swagger-ui/index.html#/`

## Seed Data

### Predefined Roles

The application comes with three predefined roles:

- **ADMIN** (Email: `admin@gmail.com`, Password: `Admin123!`)
- **MANAGER**
- **EMPLOYEE**

### Creating New Roles and Permissions

You can create new roles and permissions if needed via the provided API endpoints.

## Application Workflow

### 1. **Create a Department**

- Before you can create any employees, you must create a department.
- Departments are required for employee creation, as each employee must belong to a department.

### 2. **Create Employees**

- When creating an employee, you must specify their role using one of the following enums: `EMPLOYEE`, `ADMIN`, `MANAGER`.
- Upon creation, the employee will receive an email with their login credentials, including a temporary password.

### 3. **Employee Login**

- Employees should log in using the credentials sent to their email.
- On the first login, they will be prompted to change their password.

### 4. **Role-Based Access Control**

- **Employee Access**: Employees can only view their own details. Any attempt to perform unauthorized actions will result in an "Access Denied" error.
- **Admin/Manager Access**: Admins and Managers have broader permissions, allowing them to manage employees, departments, and roles.

## Testing

### Integration Tests

The application includes integration tests to ensure that the various components work together correctly. These tests are crucial for verifying that the application functions as expected in a production-like environment.

## Conclusion

This Employee Management System provides a comprehensive solution for managing employees and departments with secure, role-based access control. Follow the steps in this guide to set up and run the application, ensuring that all components are properly configured and operational.
