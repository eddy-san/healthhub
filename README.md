
# HealthHub

![Java](https://img.shields.io/badge/Java-21-blue)
![Jakarta EE](https://img.shields.io/badge/JakartaEE-10-orange)
![WildFly](https://img.shields.io/badge/WildFly-39-red)
![Docker](https://img.shields.io/badge/Docker-ready-blue)
![License](https://img.shields.io/badge/license-MIT-green)

HealthHub is a **secure Jakarta EE web application** for managing portal access, event registrations, and clinical data workflows.

The project demonstrates a **modern enterprise stack**:

- Jakarta EE
- WildFly
- SQL Server
- Liquibase
- Docker
- Maven

The entire stack runs locally with **one-command deployment**.

---

# Architecture

Browser  
↓  
WildFly (Jakarta EE / JSF)  
↓  
JDBC Datasource  
↓  
SQL Server (Docker)

Containers:

healthhub-app  
healthhub-sql

---

# Tech Stack

Backend: Jakarta EE  
Application Server: WildFly 39  
Database: SQL Server 2022  
Migrations: Liquibase  
Containerization: Docker  
Build: Maven

---

# Project Structure
```
HealthHub
│
├─ docker
│  └─ wildfly
│     ├─ modules
│     │  └─ com/microsoft/sqlserver/main
│     │        module.xml
│     │        mssql-jdbc.jar
│     ├─ standalone.xml
│     ├─ Dockerfile
│
├─ src
│  └─ main
│     ├─ java
│     ├─ resources
│     └─ webapp
│
├─ docker-compose.yml
├─ deploy.cmd
├─ reset.cmd
├─ pom.xml
└─ README.md
```
---

# Quick Start

Reset environment

reset.cmd

Deploy application

deploy.cmd

Application:

http://localhost:8080/healthhub

---

# Default Login

Configured in `.env`

APP_LOGIN_USER=admin  
APP_LOGIN_PASSWORD=admin

---

# Environment Variables

Example `.env`

MSSQL_SA_PASSWORD=StrongPassword123!  
APP_LOGIN_USER=admin  
APP_LOGIN_PASSWORD=admin

---

# Docker

Check containers

docker ps

Logs

docker logs healthhub-app  
docker logs healthhub-sql

---

# Database

SQL Server runs inside Docker.

Connection:

localhost:1433  
database: healthhub_db  
user: sa

WildFly datasource:

java:/jdbc/HealthHubDS

---

# Liquibase

Run migrations manually:

mvn -Pupdate-schema process-resources

---

# Security Features

Current:

- Login authentication
- Containerized database
- Environment based secrets

Planned:

- Math CAPTCHA
- Honeypot bot detection
- Login rate limiting
- Session timeout
- Audit log table

---

# API

Future endpoint

GET /api/health

Example response

{
"status": "UP",
"database": "connected"
}

---

# Roadmap

Multi Organizer Event System

Organizer  
Event  
User  
Registration

Audit log table

audit_log  
id  
timestamp  
user_id  
action  
entity  
entity_id  
ip

---

# License

MIT License

---

# Author

Eduard Roth  

