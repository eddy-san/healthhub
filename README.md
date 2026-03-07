# HealthHub

![Java](https://img.shields.io/badge/Java-21-blue)
![JakartaEE](https://img.shields.io/badge/JakartaEE-WildFly-orange)
![Build](https://img.shields.io/badge/build-maven-success)
![Database](https://img.shields.io/badge/database-SQL%20Server-red)
![License](https://img.shields.io/badge/license-TBD-lightgrey)

HealthHub is a Jakarta EE backend platform for managing clinical study
data, patient information, and secure authentication.

The system is designed with a clean enterprise architecture emphasizing
reproducibility, security and deterministic deployments.

Technologies:

-   Java 21
-   Jakarta EE
-   WildFly
-   JPA / Hibernate
-   Liquibase
-   SQL Server
-   Docker
-   Maven

------------------------------------------------------------------------

# Architecture

Controller / REST\
↓\
Service Layer\
↓\
Repository Layer (JPA)\
↓\
Database (SQL Server)

------------------------------------------------------------------------

# Security

Authentication uses **PBKDF2 password hashing**.

Format:

pbkdf2$iterations$salt\$hash

Example:

pbkdf2$120000$e+xOxYr0+zrcmMndQnU9pQ==\$mM1f9Xn4SLowMQabjWhbJNG3kTDz/mFS+U3rDrthLtA=

Features:

-   salted hashing
-   configurable iterations
-   no plaintext password storage

------------------------------------------------------------------------

# Database Management

Database schema is managed with **Liquibase**.

Migration example:

010_tables.sql\
015_seed_roles.sql\
020_procs.sql\
025_seed_admin.sql

Advantages:

-   versioned schema
-   reproducible migrations
-   audit‑friendly database changes

------------------------------------------------------------------------

# Development Workflow

Two scripts simplify development.

## Reset environment

reset.cmd

This will:

1.  reset Docker volumes
2.  start SQL Server
3.  wait until DB is ready
4.  apply Liquibase migrations

------------------------------------------------------------------------

## Deploy application

deploy.cmd

Steps:

-   Maven build
-   start WildFly if needed
-   deploy WAR

The deploy script can be executed multiple times safely during
development.

------------------------------------------------------------------------

# Default Access

Admin user is seeded via Liquibase.

username: admin\
roles: ADMIN

User roles are stored in:

app_role

User assignments:

app_user_role

------------------------------------------------------------------------

# Project Structure

src/main/java

-   auth
-   bootstrap
-   domain
-   persistence
-   service
-   tools

src/main/resources/db/changelog

------------------------------------------------------------------------

# Roadmap / Open Tasks

## Health Endpoint

GET /api/health

Used for:

-   monitoring
-   load balancers
-   Kubernetes readiness checks

Example response:

{ "status": "UP" }

------------------------------------------------------------------------

## Audit Logging

Suggested schema:

audit_log

id\
timestamp\
user_id\
action\
entity\
entity_id\
ip_address

Example events:

LOGIN_SUCCESS\
LOGIN_FAILED\
PATIENT_EXPORT\
PATIENT_UPDATE

------------------------------------------------------------------------

## Login Rate Limiting

Protection against brute force attacks.

Example:

max 5 login attempts per minute

------------------------------------------------------------------------

## Session Timeout

Recommended timeout:

30 minutes idle

------------------------------------------------------------------------

## CI/CD Pipeline

Planned GitHub Action workflow:

push → build → test → package → deploy

------------------------------------------------------------------------

# Philosophy

HealthHub focuses on explicit enterprise architecture.

Key principles:

-   minimal framework magic
-   deterministic builds
-   database migrations via Liquibase
-   strong authentication

Stack:

Java\
Jakarta EE\
WildFly\
Liquibase\
SQL Server\
Docker

------------------------------------------------------------------------

# License

TBD
