# HealthHub

HealthHub is an experimental **Jakarta EE platform for integrating
health data and wearable devices**.

🌐 **Live instance:** https://healthhub.roth-it-solutions.de

![JakartaEE](https://img.shields.io/badge/JakartaEE-10-orange)
![WildFly](https://img.shields.io/badge/WildFly-Application%20Server-red)
![SQL Server](https://img.shields.io/badge/SQL%20Server-Database-blue)
![Liquibase](https://img.shields.io/badge/Liquibase-DB%20Migration-green)
![Docker](https://img.shields.io/badge/Docker-Container-blue)
![Traefik](https://img.shields.io/badge/Traefik-Reverse%20Proxy-purple)
![GitHub
Actions](https://img.shields.io/badge/GitHub%20Actions-CI%2FCD-black)

------------------------------------------------------------------------

# Vision

HealthHub explores how software platforms can support:

-   wearable sensor integration
-   longitudinal health monitoring
-   research data pipelines
-   digital twins in medicine

Traditional healthcare systems capture data only at specific
timepoints.\
Wearables enable **continuous longitudinal health data**, closing this
gap.

------------------------------------------------------------------------

# Live Demo

A running instance of the platform is available at:

https://healthhub.roth-it-solutions.de

The landing page provides:

-   Admin login
-   API overview
-   system status information

------------------------------------------------------------------------

# Architecture

Browser → Traefik → WildFly → HealthHub → SQL Server

## Infrastructure

-   🌐 Traefik Reverse Proxy
-   🐳 Docker Deployment
-   ☕ Jakarta EE on WildFly
-   🗄 Microsoft SQL Server with JPA
-   🔁 Liquibase Database Migrations
-   ⚙ GitHub Actions CI/CD

------------------------------------------------------------------------

# Security

HealthHub applies multiple layers of protection.

-   🔐 Admin Session Login
-   🔐 JWT API Login
-   🚫 Role Separation (ADMIN / PATIENT)
-   🛡 Traefik RateLimit
-   🛡 Traefik Fail2Ban
-   🧱 AuthFilter for `/admin` routes

------------------------------------------------------------------------

# User Interface

The platform provides a lightweight administration interface.

-   Landing Page
-   Admin Login
-   Admin Dashboard
-   Navigation

Administration modules

-   Patients
-   Measurements
-   Connectors

------------------------------------------------------------------------

# API

Current API endpoints

    POST /api/auth/login
    POST /api/v1/measurement
    GET  /api/v1/measurements

Example login request

    POST /api/auth/login

Example curl request

    curl -X POST "http://localhost:18080/api/auth/login" \
    -H "Content-Type: application/json" \
    -d '{"username":"admin","password":"admin123!"}'

Example response

    {
      "token": "JWT_TOKEN",
      "tokenType": "Bearer",
      "expiresIn": 3600
    }

Using the token

    curl -X GET "http://localhost:18080/api/v1/measurements/me" \
    -H "Authorization: Bearer JWT_TOKEN"

------------------------------------------------------------------------

# Deployment

HealthHub uses an automated deployment pipeline.

Deployment flow

1.  Push to `main`
2.  Maven build
3.  SSH deployment to server
4.  Docker container rebuild
5.  Liquibase schema migration
6.  Application health check

Infrastructure features

-   automatisches Deploy
-   Reverse Proxy Routing
-   TLS via Let's Encrypt
-   öffentliches Live-System

------------------------------------------------------------------------

# Login Protection

Login endpoints are protected using Traefik middleware.

Protection mechanisms:

| Attack | Protection |
|------|------------|
| Bruteforce | rateLimit |
| Burst Attack | inFlightReq |
| Credential Stuffing | rateLimit |
| Bot Spam | rateLimit + inFlightReq |
| Automated attacks | Fail2Ban |


------------------------------------------------------------------------

# Local Development

Clone repository

    git clone https://github.com/eddy-san/healthhub.git
    cd healthhub

Helper scripts included:

-   `reset.cmd` / `reset.sh` -- reset containers and database
-   `deploy.cmd` / `deploy.sh` -- build and start the application

Run locally

    deploy.cmd

Application starts at

    http://localhost:18080

------------------------------------------------------------------------

# Roadmap

## Platform

-   User management
-   OAuth authentication
-   REST / GraphQL API

## Wearables

-   Garmin integration
-   Apple Health integration
-   wearable ingestion pipeline
-   longitudinal analytics

## Research

-   digital twin prototype
-   patient trajectory modelling
-   clinical data warehouse integration
-   study cohort management

## Infrastructure

-   automated backups
-   monitoring & metrics
-   Kubernetes deployment option

------------------------------------------------------------------------

# License

MIT License

------------------------------------------------------------------------

# Author

Eduard Roth


------------------------------------------------------------------------


# Build and deploy status

[![Build](https://github.com/eddy-san/healthhub/actions/workflows/maven.yml/badge.svg)](https://github.com/eddy-san/healthhub/actions/workflows/maven.yml)
[![Deploy](https://github.com/eddy-san/healthhub/actions/workflows/deploy.yml/badge.svg)](https://github.com/eddy-san/healthhub/actions/workflows/deploy.yml)


