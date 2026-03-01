# HealthHub

![Maven Build](https://github.com/eddy-san/healthhub/actions/workflows/maven.yml/badge.svg)

HealthHub is a Jakarta EE 11 web application with an admin backend and
frontend for collecting and managing health / wearable data (e.g. Garmin
Connect).

------------------------------------------------------------------------

## 🚀 Tech Stack

-   Jakarta EE 11
-   JSF (Server Faces)
-   CDI (Contexts and Dependency Injection)
-   JAX-RS (REST API)
-   Maven
-   WildFly 39
-   Java 21

------------------------------------------------------------------------

## 🏗 Project Structure

src/main/java/de.healthhub │ ├── api → REST endpoints\
├── model → JPA entities\
├── service → Business logic\
└── web → JSF controller (backing beans)\
├── admin\
└── app

src/main/webapp │ ├── admin → Admin views\
├── app → Frontend views\
├── index.xhtml\
└── WEB-INF\
└── web.xml

------------------------------------------------------------------------

## ▶️ Build

Linux / macOS:

    ./mvnw clean package

Windows:

    .\mvnw.cmd clean package

------------------------------------------------------------------------

## 🚀 Deployment

Copy the generated WAR file to:

    wildfly-39.0.1.Final/standalone/deployments/

Or use WildFly auto-deploy in development mode.

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

HealthHub follows a classic MVC structure:

-   Model → model\
-   View → webapp\
-   Controller → web\
-   Service Layer → service

------------------------------------------------------------------------

## 🔜 Roadmap

-   [ ] JPA + persistence layer\
-   [ ] Database integration (H2 / PostgreSQL)\
-   [ ] Role-based security (Admin/User)\
-   [ ] REST API for wearable data\
-   [ ] Docker setup

------------------------------------------------------------------------

## 👤 Author

Eddy -- Computer Scientist\
Health data & clinical process analytics
