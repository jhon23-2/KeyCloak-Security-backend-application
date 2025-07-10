# 🔐 KeyCloak Security Backend Application

This project is a secure backend system built using **Spring Boot**, integrated with **Keycloak** for authentication and authorization. It also includes a **Docker Compose** setup with Keycloak, PostgreSQL, and the backend, making it easy to run locally or deploy in development environments.

---

## ✅ Features

- 🔒 JWT Authentication using Keycloak
- 🔑 Role-based access control (`ROLE_USER`, `ROLE_ADMIN`)
- 📦 RESTful APIs protected by Spring Security
- 🐳 Docker Compose setup (PostgreSQL + Keycloak + Spring Boot)
- 🔄 Token verification and security context mapping

---

## 📁 Project Structure
├── src
│   ├── main
│   │   ├── java/.../config       → Security, Keycloak config
│   │   ├── java/.../controller   → REST endpoints
│   │   └── resources
│   │       └── application.yml   → Keycloak & DB settings
├── Dockerfile                    → Backend container setup
├── docker-compose.yaml          → Keycloak, DB & backend orchestration


## 📋 Prerequisites

- Java 17+
- Maven
- Docker & Docker‑Compose

---

## 🛠️ Local Development Setup

1. Clone the repo:
   ```bash
   git clone https://github.com/jhon23-2/KeyCloak-Security-backend-application.git
   cd KeyCloak-Security-backend-application

