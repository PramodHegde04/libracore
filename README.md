# LibraCore – Library Management System

**LibraCore** is a RESTful Library Management System built using Java 17 and Spring Boot. It provides secure APIs for user authentication, book management, and book issue/return operations.

The application uses JWT authentication, role-based authorization, Spring Data JPA, Hibernate, and MySQL. It is containerized using Docker and deployed on Render with a managed MySQL database hosted on Aiven.

## Live Deployment

- **Production API:** https://libracore-5yzm.onrender.com
- **Swagger UI:** https://libracore-5yzm.onrender.com/swagger-ui/index.html
- **GitHub Repository:** https://github.com/PramodHegde04/libracore

The production root URL is a REST API endpoint and is not intended to display a web page. Swagger UI is the recommended interface for exploring and testing the API.

## Features

### Authentication & Authorization
- User registration and login
- JWT-based Bearer authentication
- Role-based authorization
- ADMIN and MEMBER roles
- Protected REST endpoints
- Secure password storage

### Book Management
- Create, read, update, and delete books
- Retrieve books by ID
- Pagination support
- Book categories
- Total-copy and available-copy tracking

### Book Transactions
- Issue books
- Return books
- Track issue status
- Track issue and return dates
- Automatically update available copies

### Deployment & Documentation
- Docker and Docker Compose
- MySQL 8+
- Render cloud deployment
- Aiven managed MySQL database
- OpenAPI / Swagger documentation
- Environment-based production configuration
- HTTPS production endpoint

## Technology Stack

| Technology | Purpose |
|---|---|
| Java 17 | Backend programming language |
| Spring Boot | Application framework |
| Spring Security | Authentication and authorization |
| JWT | Stateless authentication |
| Spring Data JPA | Database access |
| Hibernate | ORM |
| MySQL 8+ | Relational database |
| Maven | Build and dependency management |
| Docker | Application containerization |
| Docker Compose | Local development environment |
| OpenAPI / Swagger | API documentation |
| Render | Backend cloud hosting |
| Aiven | Managed MySQL database hosting |
| Git & GitHub | Version control and source code management |

## Architecture

```text
                  Client
                    |
                    v
             Swagger UI / REST
                    |
                    v
           Spring Boot Application
                    |
          +---------+---------+
          |                   |
          v                   v
    Spring Security       Controllers
          |                   |
          v                   v
         JWT              Service Layer
                              |
                              v
                       Repository Layer
                              |
                              v
                         MySQL Database
```

### Production Deployment Architecture

```text
                 Internet
                    |
                    v
              Render HTTPS
                    |
                    v
            Spring Boot API
             Java 17 / Docker
                    |
                    | JDBC + SSL
                    v
              Aiven MySQL
              Managed Database
```

## REST API Endpoints

### Authentication

| Method | Endpoint | Description |
|---|---|---|
| POST | `/auth/register` | Register a new user |
| POST | `/auth/login` | Login and receive a JWT |

### Books

| Method | Endpoint | Description |
|---|---|---|
| GET | `/books` | Get paginated books |
| GET | `/books/{id}` | Get a book by ID |
| POST | `/books` | Create a book |
| PUT | `/books/{id}` | Update a book |
| DELETE | `/books/{id}` | Delete a book |

### Transactions

| Method | Endpoint | Description |
|---|---|---|
| POST | `/books/{bookId}/issue` | Issue a book |
| POST | `/books/{bookId}/return` | Return a book |

## Authentication Flow

```text
Client
  |
  | POST /auth/login
  v
Spring Boot
  |
  v
Validate Credentials
  |
  v
Generate JWT
  |
  v
Client Receives Token
  |
  | Authorization: Bearer <JWT>
  v
Spring Security
  |
  v
Protected API
```

## Book Issue & Return Workflow

When a book is issued, its available-copy count decreases.

```text
5 Total / 5 Available
          |
        ISSUE
          |
          v
5 Total / 4 Available
```

When the book is returned:

```text
5 Total / 4 Available
          |
        RETURN
          |
          v
5 Total / 5 Available
```

## Database

LibraCore uses MySQL with Spring Data JPA and Hibernate.

Main domain tables include:

- `users`
- `books`
- `book_issues`

### Production Database

The production database is hosted on Aiven MySQL.

The application connects to the database using JDBC and environment-based configuration. SSL is enabled for secure database communication.

### Local Database

For local Docker development, MySQL runs in a Docker container and is accessible through the Docker network.

The local MySQL port is configured separately from the production database.

## Configuration

Production configuration is managed through environment variables.

| Environment Variable | Description |
|---|---|
| `DB_URL` | MySQL JDBC connection URL |
| `DB_USERNAME` | Database username |
| `DB_PASSWORD` | Database password |
| `JWT_SECRET` | Secret used for JWT signing and verification |
| `PORT` | Application server port |
| `APP_BASE_URL` | Public application URL |

**Security:** Never commit real database credentials, JWT secrets, API keys, or other sensitive information to GitHub.

## Run Locally

### Prerequisites

- Java 17
- Git
- Docker Desktop
- Maven (optional, as the project includes Maven Wrapper)

### Clone Repository

```bash
git clone https://github.com/PramodHegde04/libracore.git

cd libracore
```

### Run Using Docker Compose

```bash
docker compose up --build
```

Application:

```text
http://localhost:8080
```

Swagger UI:

```text
http://localhost:8080/swagger-ui/index.html
```

### Run Using Maven

**Windows:**

```powershell
.\mvnw.cmd spring-boot:run
```

**Linux/macOS:**

```bash
./mvnw spring-boot:run
```

## Testing

Run the test suite using Maven Wrapper.

**Windows:**

```powershell
.\mvnw.cmd clean test
```

**Linux/macOS:**

```bash
./mvnw clean test
```

The project includes application, authentication, book integration, and security tests.

## Docker Architecture

```text
+--------------------------+
|       LibraCore App      |
|    Spring Boot / Java 17 |
|        Port 8080         |
+------------+-------------+
             |
             | JDBC
             v
+--------------------------+
|         MySQL 8          |
|      Port 3306           |
+--------------------------+
```

The Dockerfile uses a multi-stage build:

```text
Maven + JDK 17
      |
      v
Build Spring Boot JAR
      |
      v
Java 17 JRE Image
      |
      v
Run app.jar
```

## Production Deployment

LibraCore is deployed using Render and Aiven.

### Deployment Platforms

- **Render:** Hosts the Spring Boot application using Docker.
- **Aiven:** Hosts the managed MySQL production database.
- **GitHub:** Stores the source code and triggers deployments when configured changes are pushed.

### Production Request Flow

```text
Internet
   |
   v
Render HTTPS
   |
   v
LibraCore Spring Boot API
   |
   | JDBC Connection
   v
Aiven MySQL Database
```

Production secrets and configuration are supplied through Render environment variables rather than committed to the repository.

## Swagger API Testing

Open the live API documentation:

https://libracore-5yzm.onrender.com/swagger-ui/index.html

Recommended testing sequence:

1. Register a user.
2. Login using the registered credentials.
3. Copy the generated JWT.
4. Authorize in Swagger.
5. Test `GET /books`.
6. Create a book.
7. Issue the book.
8. Verify `availableCopies`.
9. Return the book.
10. Verify `availableCopies` again.

## Project Structure

```text
libracore/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/pramod/library/
│   │   │       ├── config/
│   │   │       ├── controller/
│   │   │       ├── dto/
│   │   │       ├── entity/
│   │   │       ├── repository/
│   │   │       ├── security/
│   │   │       ├── service/
│   │   │       └── LibraryManagementSystemApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       ├── java/
│       └── resources/
├── Dockerfile
├── docker-compose.yml
├── pom.xml
├── mvnw
├── mvnw.cmd
└── README.md
```

## Links

- **GitHub:** https://github.com/PramodHegde04/libracore
- **Live API:** https://libracore-5yzm.onrender.com
- **Swagger UI:** https://libracore-5yzm.onrender.com/swagger-ui/index.html


## Author

**Pramod Hegde**

## License

This project is intended for educational, portfolio, and demonstration purposes.
