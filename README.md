LibraCore

Secure Library Management & Lending Platform

LibraCore is a secure backend application for managing library books, users, inventory, and lending operations. It is built with Java 17 and Spring Boot and exposes REST APIs secured with JWT authentication and role-based authorization.

Features

User registration and login

JWT-based authentication

Role-based authorization with ADMIN and MEMBER roles

Book creation, update, deletion, and retrieval

Book search, pagination, and sorting

Book inventory tracking

Book issue and return workflow

Prevention of duplicate active book issues

Availability tracking

Centralized exception handling

MySQL database persistence

JPA/Hibernate ORM

Swagger/OpenAPI documentation

Unit, controller, integration, and security tests

Dockerized Spring Boot application

Docker Compose setup with MySQL

Tech Stack

Technology

Purpose

Java 17

Backend development

Spring Boot

Application framework

Spring Security

Authentication and authorization

JWT

Stateless authentication

Spring Data JPA

Data access

Hibernate

ORM

MySQL 8

Relational database

Maven

Build and dependency management

Docker

Containerization

Docker Compose

Multi-container orchestration

Swagger / OpenAPI

API documentation

JUnit

Automated testing

Architecture

                         Client
                           |
                           v
                  +----------------+
                  | Spring Security|
                  +----------------+
                           |
                    JWT Validation
                           |
                    Role Authorization
                           |
                           v
                  +----------------+
                  | REST Controllers|
                  +----------------+
                           |
                           v
                  +----------------+
                  | Service Layer  |
                  +----------------+
                           |
                           v
                  +----------------+
                  | Repository Layer|
                  +----------------+
                           |
                           v
                  +----------------+
                  | JPA / Hibernate|
                  +----------------+
                           |
                           v
                  +----------------+
                  |     MySQL      |
                  +----------------+

Authentication & Authorization

LibraCore uses JWT-based stateless authentication.

Authentication Flow

Register / Login
       |
       v
Validate Credentials
       |
       v
Generate JWT
       |
       v
Client Stores Token
       |
       v
Authorization: Bearer <JWT>
       |
       v
Spring Security
       |
       v
Validate JWT
       |
       v
Extract User + Role
       |
       v
Access Protected Endpoint

Roles

ADMIN

Manage books

Perform administrative operations

MEMBER

View books

Issue books

Return books

Example:

POST /books

ADMIN  -> Allowed
MEMBER -> 403 Forbidden

Requests without valid authentication receive 401 Unauthorized.

Authenticated users without sufficient permissions receive 403 Forbidden.

REST API

Authentication

Method

Endpoint

Description

POST

/auth/register

Register a new user

POST

/auth/login

Login and receive JWT

Books

Method

Endpoint

Access

Description

GET

/books

Authenticated

Get books with pagination/search/sorting

GET

/books/{id}

Authenticated

Get a book by ID

POST

/books

ADMIN

Create a book

PUT

/books/{id}

ADMIN

Update a book

DELETE

/books/{id}

ADMIN

Delete a book

Lending

Method

Endpoint

Access

Description

POST

/books/{id}/issue

Authenticated

Issue a book

POST

/books/{id}/return

Authenticated

Return a book

Lending Workflow

Issue

Available Book
      |
      v
Issue Request
      |
      v
Validate User
      |
      v
Validate Book
      |
      v
Check Available Copies
      |
      v
Check Existing Active Issue
      |
      v
Create Book Issue
      |
      v
availableCopies - 1
      |
      v
Status = ISSUED

Return

Issued Book
      |
      v
Return Request
      |
      v
Find Active Issue
      |
      v
Set Return Date
      |
      v
Status = RETURNED
      |
      v
availableCopies + 1

The system prevents issuing an unavailable book, issuing the same book twice while an active issue exists, and returning a book that is not currently issued.

Inventory Management

Each book maintains:

totalCopies
availableCopies

Example:

Before Issue:
totalCopies     = 15
availableCopies = 15

After Issue:
totalCopies     = 15
availableCopies = 14

After Return:
totalCopies     = 15
availableCopies = 15

Database Design

+-------------+
|    User     |
+-------------+
       |
       | 1
       |
       | *
+-------------+
|  BookIssue  |
+-------------+
       |
       | *
       |
       | 1
+-------------+
|    Book     |
+-------------+

User

Stores user ID, name, email, password, and role.

Book

Stores book ID, title, author, ISBN, category, total copies, and available copies.

BookIssue

Stores issue ID, user, book, issue date, return date, and issue status.

Project Structure

libracore/
├── src/
│   ├── main/
│   │   ├── java/com/pramod/library/
│   │   │   ├── config/
│   │   │   ├── controller/
│   │   │   ├── dto/
│   │   │   ├── entity/
│   │   │   ├── exception/
│   │   │   ├── mapper/
│   │   │   ├── repository/
│   │   │   ├── security/
│   │   │   └── service/
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/com/pramod/library/
├── Dockerfile
├── docker-compose.yml
├── pom.xml
├── mvnw
├── mvnw.cmd
├── .gitignore
└── README.md

Docker

LibraCore runs as a multi-container application using Docker Compose.

+------------------------------------------+
|              Docker Compose              |
|                                          |
|  +------------------+                    |
|  |   library-app    |                    |
|  |   Spring Boot    |                    |
|  |   Port: 8080     |                    |
|  +--------+---------+                    |
|           |                              |
|           | JDBC                         |
|           v                              |
|  +------------------+                    |
|  |  library-mysql   |                    |
|  |    MySQL 8.0     |                    |
|  |    Port: 3306    |                    |
|  +------------------+                    |
+------------------------------------------+

Build and Start

docker compose up -d --build

Check Containers

docker compose ps

View Application Logs

docker compose logs library-app

Stop Containers

docker compose down

The MySQL service uses a Docker volume for persistent database storage.

Configuration

Sensitive configuration should be provided through environment variables.

Example:

DB_USERNAME=root
DB_PASSWORD=your_database_password
JWT_SECRET=your_jwt_secret

Do not commit real credentials, passwords, JWT secrets, or API keys to the repository.

The .env file is excluded through .gitignore.

Running the Project

Prerequisites

Java 17

Docker Desktop

MySQL 8 if running without Docker

Git

Clone

git clone https://github.com/YOUR_USERNAME/libracore.git
cd libracore

Run with Docker

docker compose up -d --build

Check:

http://localhost:8080

Run Without Docker

Windows:

.\mvnw.cmd spring-boot:run

Linux/macOS:

./mvnw spring-boot:run

Swagger / OpenAPI

Interactive API documentation:

http://localhost:8080/swagger-ui/index.html

Swagger can be used to register users, login, authorize using JWT, test protected endpoints, test ADMIN/MEMBER authorization, and test book and lending operations.

Testing

The project includes application context, controller, authentication integration, book integration, security integration, book service, and book issue service tests.

Windows:

.\mvnw.cmd test

Linux/macOS:

./mvnw test

Error Handling

LibraCore uses centralized exception handling through GlobalExceptionHandler.

Example:

{
  "status": 400,
  "message": "User has already issued this book",
  "timestamp": "2026-08-22T04:00:27"
}

Handled scenarios include user not found, book not found, duplicate active issue, unavailable book, invalid requests, unauthorized requests, and insufficient permissions.

HTTP Status Codes

Status

Meaning

200

Request successful

201

Resource created

400

Invalid request or business rule violation

401

Authentication required or invalid

403

Authenticated but insufficient permissions

404

Resource not found

Security

Security mechanisms implemented in LibraCore include:

JWT authentication

Spring Security

Role-based authorization

Password hashing

Protected REST endpoints

Stateless authentication

Environment-based configuration for sensitive values

Centralized security configuration

Future Improvements

Refresh token support

Email notifications

Due-date management

Overdue fine calculation

Book reservation and waitlist

User lending history

Admin dashboard

Redis caching

Rate limiting

CI/CD pipeline

Cloud deployment

Application monitoring

Centralized logging

Metrics and observability

Author

Pramod Hegde

Java Backend Developer

Technologies: Java | Spring Boot | Spring Security | REST APIs | JPA/Hibernate | MySQL | Docker

License

This project is intended for educational and portfolio purposes.
