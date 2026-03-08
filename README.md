# HealthHub

![Java](https://img.shields.io/badge/Java-21-blue)
![Jakarta EE](https://img.shields.io/badge/JakartaEE-WildFly-orange)
![Build](https://img.shields.io/badge/build-maven-success)
![Database](https://img.shields.io/badge/database-SQL%20Server-red)
![License](https://img.shields.io/badge/license-MIT-lightgrey)

HealthHub is a Jakarta EE backend platform for managing clinical study data, patient information, and secure authentication.

The stack is intentionally explicit and enterprise-oriented:

- WildFly 39
- Jakarta EE 11
- JPA / Hibernate
- Liquibase
- SQL Server 2022
- Docker / Docker Compose
- Maven Wrapper

## Architecture

```text
Browser
  ↓
JSF / Web Layer
  ↓
AuthenticationService / Application Services
  ↓
Repositories (JPA)
  ↓
SQL Server
```

## Security

Authentication uses PBKDF2 password hashing.

```text
pbkdf2$iterations$salt$hash
```

Passwords are never stored in plaintext.

## Database management

Schema management is handled by Liquibase.

Migration sequence:

```text
010_tables.sql
015_seed_roles.sql
020_procs.sql
025_seed_admin.sql
```

This keeps database changes reproducible and auditable.

## Container setup

All container-related files now live under the `docker/` directory.

```text
docker/
 ├─ docker-compose.yml
 └─ wildfly/
     ├─ Dockerfile
     ├─ configure.cli
     └─ modules/com/microsoft/sqlserver/main/module.xml
```

The project runs with two containers:

- `healthhub-sql` → SQL Server
- `healthhub-app` → WildFly + deployed WAR + JDBC driver + datasource

Inside Docker, the datasource connects to SQL Server via the Compose service name:

```text
healthhub-sql:1433
```

## Local development workflow

### 1. Reset database

```bat
reset.cmd
```

This will:

1. stop and remove containers and volumes
2. start SQL Server
3. wait for readiness
4. execute Liquibase migrations

Internally it uses:

```bat
docker compose --env-file .env -f docker\docker-compose.yml down -v --remove-orphans
docker compose --env-file .env -f docker\docker-compose.yml up -d healthhub-sql
```

### 2. Build and deploy app

```bat
deploy.cmd
```

This will:

1. build the WAR with Maven Wrapper
2. copy the SQL Server JDBC driver from the local Maven cache into the Docker build context
3. build the WildFly container image
4. start / update the application container

Application URL:

```text
http://localhost:8080/healthhub/
```

## Environment variables

Create a `.env` file based on `.env.example`:

```env
MSSQL_SA_PASSWORD=YourStr0ng!Passw0rd
```

## Default access

An initial admin user is created via Liquibase seed data.

- username: `admin`
- role: `ADMIN`

## Project structure

```text
src/main/java
 ├─ auth
 ├─ domain
 ├─ persistence
 ├─ web
 └─ tools

src/main/resources/db/changelog

docker/
 ├─ docker-compose.yml
 └─ wildfly/
     ├─ Dockerfile
     ├─ configure.cli
     └─ modules/com/microsoft/sqlserver/main/module.xml
```

## Roadmap

Open next steps:

- Health endpoint (`/api/health`)
- persistent audit log
- login rate limiting
- session timeout
- GitHub Actions CI/CD pipeline

## License

MIT
