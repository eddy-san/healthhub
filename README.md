# HealthHub

HealthHub is an experimental **Jakarta EE platform for integrating
health data and wearable devices**.

🌐 **Live instance:** https://healthhub.roth-it-solutions.de

![JakartaEE](https://img.shields.io/badge/JakartaEE-10+-orange)
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

## High-Level

Browser → Traefik → WildFly → HealthHub → SQL Server

## Modular Architecture

    auth/
    measurement/
    health/
    shared/

### Module Structure Rule

-   api → external interface (REST endpoints)
-   service → business logic
-   repository → persistence layer
-   model → data structures
-   security → authentication & authorization

------------------------------------------------------------------------

# Infrastructure

-   🌐 Traefik Reverse Proxy
-   🐳 Docker Deployment
-   ☕ Jakarta EE on WildFly
-   🗄 Microsoft SQL Server with JPA
-   🔁 Liquibase Database Migrations
-   ⚙ GitHub Actions CI/CD

------------------------------------------------------------------------

# Security

HealthHub applies multiple layers of protection.

-   🔐 OpenID Connect (OIDC) via Keycloak
-   🔁 Authorization Code Flow (confidential client)
-   🎟 JWT access tokens (RS256 signed)
-   🔍 Role-based access control (RBAC)
-   🧱 Server-side enforcement via WildFly Elytron OIDC
-   🌐 TLS termination via Traefik (Let's Encrypt)
-   🛡 Layered defense (RateLimit, Fail2Ban)

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

HealthHub uses **Keycloak (OIDC)** for authentication.\
API access is secured via **JWT access tokens**.

## Authentication

-   Browser-based clients use the Authorization Code Flow
-   Machine-to-machine or device integrations may use alternative flows
    (e.g. client credentials or token exchange)

Example token endpoint:

POST
https://auth.roth-it-solutions.de/realms/healthhub/protocol/openid-connect/token

------------------------------------------------------------------------

## Endpoints

POST /api/v1/measurements\
GET /api/v1/measurements/me

------------------------------------------------------------------------

## Authorization

All API endpoints require a valid JWT access token:

-   Role `PATIENT` → allowed to submit and read own measurements
-   Role `ADMIN` → full access (administration, monitoring)

------------------------------------------------------------------------

## Example Request

``` bash
curl -X GET "https://healthhub.roth-it-solutions.de/api/v1/measurements/me"   -H "Authorization: Bearer ACCESS_TOKEN"
```

------------------------------------------------------------------------

## Example Measurement Upload

``` bash
curl -X POST "https://healthhub.roth-it-solutions.de/api/v1/measurements"   -H "Authorization: Bearer ACCESS_TOKEN"   -H "Content-Type: application/json"   -d '{
    "type": "heart_rate",
    "value": 72,
    "timestamp": "2026-03-29T10:00:00Z"
  }'
```

------------------------------------------------------------------------

## Security Model

-   JWT access tokens (issued and signed by Keycloak) are validated by
    WildFly (Elytron OIDC)
-   Roles are extracted from the token (via Keycloak role mappings)
-   Access control is enforced via `@RolesAllowed`

## Trust Model

-   Identity Provider: Keycloak
-   Resource Server: WildFly (HealthHub)
-   Reverse Proxy: Traefik (TLS termination)
-   Backend: SQL Server (internal network)

All trust boundaries are enforced via TLS and token validation.

------------------------------------------------------------------------

## Notes

-   No custom login endpoint is implemented in HealthHub
-   Authentication is fully delegated to Keycloak
-   API is stateless and does not use server-side sessions

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

Authentication endpoints (Keycloak) and public entry points are
protected using Traefik middleware.

Protection mechanisms:

| Attack              | Protection                  |
|---------------------|-----------------------------|
| Bruteforce          | rateLimit                   |
| Burst Attack        | inFlightReq                 |
| Credential Stuffing | rateLimit                   |
| Bot Spam            | rateLimit + inFlightReq     |
| Automated attacks   | Fail2Ban                    |

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
