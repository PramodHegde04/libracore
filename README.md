# LibraCore

## Library Management System

LibraCore is a RESTful Library Management System built with **Java 17** and **Spring Boot**. It provides secure APIs for user authentication, book management, and book issue/return operations.

The application uses JWT authentication, role-based authorization, Spring Data JPA/Hibernate, and MySQL. It is containerized with Docker and deployed to Railway.

## Live Deployment

- **Production API:** https://libracore-production.up.railway.app
- **Swagger UI:** https://libracore-production.up.railway.app/swagger-ui/index.html

> The production root URL is a backend REST API and is not intended to display a web page. Swagger UI is the recommended interface for exploring and testing the API.

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
- Get books by ID
- Pagination
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
- MySQL 8
- Railway deployment
- OpenAPI / Swagger UI
- Environment-based production configuration
- HTTPS production endpoint

## Technology Stack

| Technology | Purpose |
|---|---|
| Java 17 | Backend language |
| Spring Boot | Application framework |
| Spring Security | Authentication and authorization |
| JWT | Stateless authentication |
| Spring Data JPA | Database access |
| Hibernate | ORM |
| MySQL 8 | Relational database |
| Maven | Build and dependency management |
| Docker | Containerization |
| Docker Compose | Local environment |
| OpenAPI / Swagger | API documentation |
| Railway | Cloud deployment |

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
              +------------+------------+
              |                         |
              v                         v
       Spring Security             Controllers
              |                         |
              v                         v
             JWT                  Service Layer
                                        |
                                        v
                                Repository Layer
                                        |
                                        v
                                  MySQL Database
```

## REST API

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
Validate credentials
  |
  v
Generate JWT
  |
  v
Client receives token
  |
  | Authorization: Bearer <JWT>
  v
Spring Security
  |
  v
Protected API
```

## Book Issue & Return

When a book is issued, its available-copy count decreases.

```text
5 total / 5 available
          |
        ISSUE
          v
5 total / 4 available
```

When it is returned:

```text
5 total / 4 available
          |
        RETURN
          v
5 total / 5 available
```

## Database

The application uses MySQL with Spring Data JPA and Hibernate.

Main domain tables include:

```text
users
books
book_issues
```

For the local Docker environment, MySQL is exposed on port `3307` and the application connects to the MySQL container over the Docker network.

## Configuration

Production configuration is supplied through environment variables.

Typical variables:

```text
DB_USERNAME
DB_PASSWORD
JWT_SECRET
PORT
```

Never commit real database credentials, JWT secrets, API keys, or other sensitive values to Git.

## Run Locally

### Prerequisites

- Java 17
- Git
- Docker Desktop

The project includes the Maven Wrapper.

### Clone

```bash
git clone https://github.com/PramodHegde04/libracore.git
cd libracore
```

### Docker Compose

```bash
docker compose up --build
```

Application:

```text
http://localhost:8080
```

Swagger:

```text
http://localhost:8080/swagger-ui/index.html
```

### Run with Maven

Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

Linux/macOS:

```bash
./mvnw spring-boot:run
```

## Testing

Windows:

```powershell
.\mvnw.cmd clean test
```

Linux/macOS:

```bash
./mvnw clean test
```

The project includes application, authentication, book integration, and security tests.

## Docker Architecture

```text
+------------------------+
|   LibraCore App        |
|   Spring Boot / Java17 |
|   Port 8080            |
+-----------+------------+
            |
            | JDBC
            v
+------------------------+
|       MySQL 8          |
|      Port 3306         |
+------------------------+
```

The Dockerfile uses a multi-stage build:

```text
Maven + JDK 17
      |
      v
Build Spring Boot JAR
      |
      v
Java 17 JRE image
      |
      v
Run app.jar
```

## Production Deployment

LibraCore is deployed on Railway.

```text
Internet
   |
   v
Railway HTTPS
   |
   v
LibraCore API
   |
   v
Railway MySQL
```

Production secrets and configuration are supplied through Railway environment variables rather than committed to the repository.

## Swagger Testing Flow

Open:

https://libracore-production.up.railway.app/swagger-ui/index.html

Recommended sequence:

```text
1. Register a user
2. Login
3. Copy the JWT
4. Authorize in Swagger
5. Test GET /books
6. Create a book
7. Issue the book
8. Verify availableCopies
9. Return the book
10. Verify availableCopies again
```

## Production Verification

The deployed application has been verified for:

- Registration
- Login
- JWT generation
- JWT authentication
- ADMIN authorization
- MEMBER authorization
- Book creation
- Book retrieval
- Book update
- Book deletion
- Book issue
- Book return
- Available-copy updates
- MySQL persistence
- Swagger API testing
- HTTPS production access

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
- **Production API:** https://libracore-production.up.railway.app
- **Swagger UI:** https://libracore-production.up.railway.app/swagger-ui/index.html

## Author

**Pramod Hegde**

## License

This project is intended for educational, portfolio, and demonstration purposes.
