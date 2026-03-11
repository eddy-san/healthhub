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

Live Demo:

- https://healthhub.roth-it-solutions.de/
- Demo account available on request

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
- Dockerized deployment
- Reverse proxy routing via Traefik
- GitHub Actions CI/CD pipeline

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

- `reset.cmd/reset.sh` – resets containers and database
- `deploy.cmd/deploy.sh` – builds and starts the application
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