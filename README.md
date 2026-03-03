# HealthHub

![Build](https://github.com/eddy-san/healthhub/actions/workflows/maven.yml/badge.svg)
![Java](https://img.shields.io/badge/Java-21-blue) ![JakartaEE](https://img.shields.io/badge/Jakarta_EE-11-red)
![Liquibase](https://img.shields.io/badge/Liquibase-4.27-orange)
![SQL_Server](https://img.shields.io/badge/SQL_Server-2022-darkred)
![License](https://img.shields.io/badge/License-MIT-green)

HealthHub is a Jakarta EE 11 web application with an admin backend and
frontend for collecting and managing health / wearable data (e.g. Garmin
Connect).

The project includes automated database provisioning and schema
migration using Docker + Microsoft SQL Server + Liquibase.

------------------------------------------------------------------------

## 🚀 Tech Stack

-   Jakarta EE 11
-   JSF (Server Faces)
-   CDI (Contexts and Dependency Injection)
-   JAX-RS (REST API)
-   Maven (Wrapper included)
-   WildFly 39
-   Java 21
-   Microsoft SQL Server 2022 (Docker)
-   Liquibase 4.27 (Schema Migration)

------------------------------------------------------------------------

## 🏗 Project Structure

    src/main/java/de/healthhub
    │
    ├── api        → REST endpoints
    ├── model      → Domain / JPA entities
    ├── service    → Business logic
    └── web        → JSF backing beans
        ├── admin
        └── app

    src/main/resources
    └── db
        └── changelog
            ├── create-db
            └── schema

    src/main/webapp
    │
    ├── admin      → Admin views
    ├── app        → Frontend views
    ├── index.xhtml
    └── WEB-INF
        └── web.xml

------------------------------------------------------------------------

## 🐳 Local Development (Full Setup)

Start database:

    docker compose up -d

Run database migrations:

    $env:JAVA_TOOL_OPTIONS="-Ddb.password=$env:MSSQL_SA_PASSWORD"
    ./mvnw -Pupdate-schema clean process-resources

Build WAR:

    ./mvnw clean package

------------------------------------------------------------------------

## 🚀 One-Click Deploy (Recommended)

Use the provided script:

Linux / Git Bash:

    ./deploy.sh

PowerShell:

    bash .\deploy.sh

The script:

1.  Starts Docker (SQL Server)
2.  Runs Liquibase migrations
3.  Builds the WAR
4.  Deploys to WildFly
5.  Triggers auto-deploy

------------------------------------------------------------------------

## 🌍 Access

After deployment:

    http://localhost:8080/healthhub-1.0-SNAPSHOT/

Admin Dashboard:

    /admin/dashboard.xhtml

Frontend:

    /app/home.xhtml

------------------------------------------------------------------------

## 📦 Architecture

HealthHub follows a classic layered architecture:

-   Model → domain objects / entities
-   View → JSF pages
-   Controller → JSF backing beans
-   Service Layer → business logic
-   Database → Versioned via Liquibase
-   Infrastructure → Docker + SQL Server

Database changes are fully version-controlled and reproducible.

------------------------------------------------------------------------

## 🔐 Database Strategy

-   Database created automatically via Liquibase
-   Tables and indexes managed in `010_tables.sql`
-   Stored procedures managed in `020_procs.sql`
-   Execution history stored in `DATABASECHANGELOG`

This ensures deterministic deployments across environments.

------------------------------------------------------------------------

## 🔜 Roadmap

-   [ ] JPA integration layer
-   [ ] Role-based security (Admin/User)
-   [ ] REST ingestion endpoint for wearable APIs
-   [ ] CI pipeline including DB migration stage
-   [ ] Production Docker setup

------------------------------------------------------------------------

## 👤 Author

Eduard Roth
