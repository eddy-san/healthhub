# HealthHub

HealthHub is a Jakarta EE web application designed as a clean, modular
architecture for managing clinical data workflows.\
The project demonstrates a modern enterprise stack using **WildFly**,
**Hibernate/JPA**, **Liquibase**, and **SQL Server** running in
**Docker**.

------------------------------------------------------------------------

## Architecture Overview

HealthHub follows a layered architecture:

Web Layer - JSF / Web Beans - Security (AuthFilter, LoginBean)

Application Layer - AuthenticationService

Domain Layer - User - Role - Patient

Persistence Layer - UserRepository - RoleRepository - PatientRepository

Infrastructure - WildFly - Liquibase migrations - SQL Server (Docker)

------------------------------------------------------------------------

## Technology Stack

Backend: Jakarta EE 11\
Application Server: WildFly 39\
ORM: Hibernate / JPA\
Database: Microsoft SQL Server\
Migrations: Liquibase\
Build Tool: Maven\
Containerization: Docker\
IDE: IntelliJ IDEA

------------------------------------------------------------------------

## Database Model

The authentication system uses a role-based access control model.

app_user\
→ app_user_role\
→ app_role

Example:

username \| role\
admin \| ADMIN

------------------------------------------------------------------------

## Authentication Flow

Login Page\
↓\
LoginBean\
↓\
AuthenticationService\
↓\
UserRepository\
↓\
SQL Server\
↓\
PasswordHasher (PBKDF2)\
↓\
UserSession

Passwords are stored using:

PBKDF2\
120000 iterations\
salt + hash

Example stored hash:

pbkdf2$120000$...

------------------------------------------------------------------------

## Running the Project

### 1. Start SQL Server

docker compose up -d

### 2. Run Liquibase migrations

./mvnw -Pupdate-schema process-resources

### 3. Build application

./mvnw clean package

### 4. Start WildFly

standalone.bat

Application:

http://localhost:8080/healthhub

------------------------------------------------------------------------

## Default Admin User

Created via Liquibase seed migration.

username: admin\
password: admin123!\
role: ADMIN

------------------------------------------------------------------------

## Project Structure

src/main/java/de/healthhub - api - auth - bootstrap - domain - auth -
patient - persistence - web - admin - app - security

------------------------------------------------------------------------

## Development Notes

Recommended workflow:

1.  Modify database schema via Liquibase
2.  Update JPA entities
3.  Implement logic in services
4.  Expose functionality via web beans / API

------------------------------------------------------------------------

## Future Features

Planned extensions:

-   Patient onboarding workflow
-   Clinical event capture
-   Export pipelines
-   Audit logging
-   REST API for mobile applications

------------------------------------------------------------------------

## License

MIT License

------------------------------------------------------------------------

## Author

Eduard Roth\
Computer Scientist \| BI \| Health Data
