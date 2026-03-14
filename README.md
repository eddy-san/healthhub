
# HealthHub
*Open platform for wearable health data integration and digital health analytics.*

[![Build](https://github.com/eddy-san/healthhub/actions/workflows/maven.yml/badge.svg)](https://github.com/eddy-san/healthhub/actions/workflows/maven.yml)
[![Deploy](https://github.com/eddy-san/healthhub/actions/workflows/deploy.yml/badge.svg)](https://github.com/eddy-san/healthhub/actions/workflows/deploy.yml)

HealthHub is an experimental **Jakarta EE platform for integrating health data and wearable devices**.

![JakartaEE](https://img.shields.io/badge/JakartaEE-10-orange)
![WildFly](https://img.shields.io/badge/WildFly-Application%20Server-red)
![SQL Server](https://img.shields.io/badge/SQL%20Server-Database-blue)
![Liquibase](https://img.shields.io/badge/Liquibase-DB%20Migration-green)
![Docker](https://img.shields.io/badge/Docker-Container-blue)
![Traefik](https://img.shields.io/badge/Traefik-Reverse%20Proxy-purple)
![GitHub Actions](https://img.shields.io/badge/GitHub%20Actions-CI%2FCD-black)

---

# Vision

HealthHub explores how software platforms can support:

- wearable sensor integration
- longitudinal health monitoring
- research data pipelines
- digital twins in medicine

Traditional healthcare systems capture data only at specific timepoints.  
Wearables enable **continuous longitudinal health data**, closing this gap.

---

# Architecture

System flow

Browser → Traefik → WildFly → HealthHub → SQL Server

| Layer | Technology |
|------|-------------|
| Reverse Proxy | Traefik |
| Application Server | WildFly |
| Backend Framework | Jakarta EE |
| Persistence | JPA |
| Database | Microsoft SQL Server |
| Migration | Liquibase |
| Containerization | Docker |
| CI/CD | GitHub Actions |

---

# Features

Current functionality

- Jakarta EE MVC architecture
- JPA persistence layer
- Liquibase database migrations
- Admin user bootstrap
- JWT authentication for API
- Dockerized deployment
- Reverse proxy routing via Traefik
- GitHub Actions CI/CD pipeline

---

# API Authentication

Example login request

```
POST /api/auth/login
```

```
curl -X POST "http://localhost:18080/api/auth/login" -H "Content-Type: application/json" -d '{"username":"admin","password":"admin123!"}'
```

Example response

```
{
  "token": "JWT_TOKEN",
  "tokenType": "Bearer",
  "expiresIn": 3600
}
```

Using the token

```
curl -X GET "http://localhost:18080/api/v1/measurements/me" -H "Authorization: Bearer JWT_TOKEN"
```

---

# CI/CD Pipeline

Deployment workflow

1. Push to `main`
2. Maven build
3. SSH deployment to server
4. Docker container rebuild
5. Liquibase schema migration
6. Application health check

Pipeline ensures

- reproducible builds
- automated deployments
- database schema versioning

---

# Local Development

Clone repository

```
git clone https://github.com/eddy-san/healthhub.git
cd healthhub
```

For convenience the repository includes:

- `reset.cmd` / `reset.sh` – resets containers and database
- `deploy.cmd` / `deploy.sh` – builds and starts the application

Run locally

```
deploy.cmd
```

Application will start at

```
http://localhost:18080
```

---

# Roadmap

## Platform

- User management
- OAuth authentication
- REST / GraphQL API

## Wearables

- Garmin integration
- Apple Health integration
- wearable ingestion pipeline
- longitudinal analytics

## Research

- digital twin prototype
- patient trajectory modelling
- clinical data warehouse integration
- study cohort management

## Infrastructure

- automated backups
- monitoring & metrics
- Kubernetes deployment option

---

# License

MIT License

---

# Author

Eduard Roth