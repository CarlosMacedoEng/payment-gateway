## Overview

This repository contains a **Payment Gateway Service** implemented with **Java 17 and Spring Boot**, designed to simulate how real payment systems are built in production environments.

Rather than being a simple CRUD project, this service is structured to progressively evolve through **clearly defined milestones**, introducing patterns commonly used in financial systems such as:

- Idempotent writes
- Event-driven architecture
- Reliable message publishing
- Ledger-style data modeling
- Observability and fault tolerance

Each milestone is self-contained, documented, and committed separately to clearly demonstrate architectural decisions and trade-offs.

---

## Architecture

The system follows a **layered, domain-centric architecture**, intentionally simple at first, and designed to evolve without breaking existing contracts.

```
API Layer
  └── REST Controllers
      └── Input Validation
          └── Service Layer
              └── Domain Logic
                  └── Persistence Layer
                      └── PostgreSQL
```

### Package Structure

```
com.example.payment_gateway
 ├── api
 │   ├── PaymentController
 │   ├── ApiExceptionHandler
 │   └── dto
 ├── domain
 │   ├── Payment
 │   └── PaymentStatus
 ├── repository
 │   └── PaymentRepository
 ├── service
 │   ├── PaymentService
 │   └── PaymentNotFoundException
```

### Architectural Principles

- Explicit domain modeling
- Clear separation of concerns
- Transaction boundaries at the service layer
- Database-backed persistence
- Infrastructure managed via Docker

---

## Milestones

### ✅ Milestone 1 — Core Payment Service (Completed)

- REST API to create and retrieve payments
- Input validation and standardized error handling
- PostgreSQL persistence using Docker
- Domain-driven structure
- Java 17 + Spring Boot 4

Endpoints:
- POST /payments
- GET /payments/{id}

---

### 🔜 Milestone 2 — Idempotency Key

- Idempotency-Key header
- Safe retries
- Payload conflict detection
- HTTP 409 handling

---

### 🔜 Milestone 3 — Event Publishing (Kafka)

- Domain events
- Kafka integration
- Event versioning

---

## Quick Start

### Prerequisites
- Java 17+
- Docker & Docker Compose

### Start infrastructure
```
docker compose up -d
```

### Run the application
```
./mvnw spring-boot:run
```

### Health check
```
curl http://localhost:8081/actuator/health
```

---

## API

### Create Payment
```
POST /payments
```

### Get Payment
```
GET /payments/{id}
```

---

## Tech Stack

- Java 17
- Spring Boot 4
- Spring Data JPA
- PostgreSQL
- Apache Kafka (future)
- Docker
- Lombok

---

## License

MIT License.
