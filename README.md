# HealthHub

![Build](https://github.com/eddy-san/healthhub/actions/workflows/maven.yml/badge.svg)
![Java](https://img.shields.io/badge/Java-21-blue) ![JakartaEE](https://img.shields.io/badge/Jakarta_EE-11-red)
![Liquibase](https://img.shields.io/badge/Liquibase-4.27-orange)
![SQL_Server](https://img.shields.io/badge/SQL_Server-2022-darkred)
![License](https://img.shields.io/badge/License-MIT-green)

HealthHub is a Jakarta EE 11 web application with JSF frontend, JPA/Hibernate persistence,
role-based login and SQL Server schema management via Liquibase.

## Auth and domain model

The project now uses a clean separation between technical identity and domain model:

- `User` → login account
- `RoleEntity` / `RoleName` → permissions (`ADMIN`, `PATIENT`, `EXPORT`)
- `Patient` → domain entity with `OneToOne` link to `User`
- `LoggedInUser` → lightweight session model
- `UserSession` → CDI session bean

This avoids JPA inheritance for user types and keeps authentication, authorization and
patient context separate.

## Project structure

```text
src/main/java/de/healthhub
├── auth
├── bootstrap
├── domain
│   ├── auth
│   └── patient
├── persistence
└── web
    ├── app
    └── security
```

## Database strategy

- Database creation via Liquibase profile `update-schema`
- DDL in `src/main/resources/db/changelog/schema/010_tables.sql`
- role seed data in `015_seed_roles.sql`
- stored procedures in `020_procs.sql`
- Hibernate schema generation disabled in `persistence.xml`

## Bootstrap admin

On application startup, `AdminBootstrap` ensures the standard roles exist and creates an admin
account if the configured username does not exist yet.

Environment variables:

```text
APP_BOOTSTRAP_ADMIN_USER=admin
APP_BOOTSTRAP_ADMIN_PASSWORD=admin123!
APP_BOOTSTRAP_ADMIN_EMAIL=admin@healthhub.local
```

## Persistence configuration

`persistence.xml` expects a WildFly datasource:

```text
java:/jdbc/HealthHubDS
```

Hibernate is used only for ORM access. DDL is managed externally.

## Local development

Start database:

```bash
docker compose up -d
```

Run schema migration:

```bash
export JAVA_TOOL_OPTIONS="-Ddb.password=$MSSQL_SA_PASSWORD"
./mvnw -Pupdate-schema clean process-resources
```

Build WAR:

```bash
./mvnw clean package
```

## Current state

Included in this version:

- JPA entities for `User`, `RoleEntity`, `Patient`
- repositories without native SQL
- `AuthenticationService`
- `PasswordHasher` with PBKDF2
- CDI session-based login context
- admin bootstrap user
- JSF login, app home and admin dashboard
