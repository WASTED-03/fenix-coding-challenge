# Multi-Tenant Commerce Ingestion API

A production-ready Java Spring Boot ingestion service designed to handle
high-throughput order and fulfillment processing for multiple
organizations (tenants). The system enforces strict data isolation using
a hierarchical domain model.

------------------------------------------------------------------------

## 🚀 Architecture Highlights

### Strict Multi-Tenancy (Hibernate 6)

-   Uses native `@TenantId` for automatic tenant-based query scoping\
-   Ensures complete database-level isolation between organizations\
-   Eliminates risk of cross-tenant data access

### Identity-Bound Security

-   Integrated Spring Security with Basic Authentication\
-   Tenant context derived from authenticated user\
-   `X-Tenant-ID` header validated against user's assigned organization

### Event-Driven Ingestion

-   Asynchronous processing using `@Async` and
    `ApplicationEventPublisher`\
-   Order persistence decoupled from background workflows\
-   Enables sub-100ms API response times under load

### API-First Development

-   Fully compliant with OpenAPI 3.0 specification\
-   DTOs and controllers generated via Maven\
-   Guarantees zero contract drift

### Intelligent Caching

-   Spring Cache used for read-heavy entities (Organizations, Websites)\
-   Reduces database load under high concurrency

### Standardized Error Handling

-   Implements RFC 7807 Problem Details\
-   Centralized exception handling via `@ControllerAdvice`\
-   Clean, consistent JSON error responses

------------------------------------------------------------------------

## 🛠️ Tech Stack

-   Java 17\
-   Spring Boot 3.x, Spring Data JPA\
-   Spring Security (Basic Auth)\
-   MySQL 8.0\
-   JUnit 5, Testcontainers\
-   Maven

------------------------------------------------------------------------

## ⚙️ Getting Started

### Prerequisites

-   Docker Desktop\
-   Java 17 or higher

------------------------------------------------------------------------

### Start Database

``` bash
docker-compose up -d
```

------------------------------------------------------------------------

### Build and Run Application

``` bash
mvn clean spring-boot:run
```

------------------------------------------------------------------------

### Run Integration Tests

``` bash
mvn test
```

------------------------------------------------------------------------

## 🔐 API Usage

### Authentication

-   Username: admin@acme.com\
-   Password: password123

------------------------------------------------------------------------

### Required Header

    X-Tenant-ID: <Organization UUID>

------------------------------------------------------------------------

### Sample Workflow

1.  POST /organizations\
2.  POST /organizations/{orgId}/websites\
3.  POST /orders

------------------------------------------------------------------------

## 📂 Project Structure

    com.commerce.ingestion
    ├── tenant
    ├── config
    ├── service
    ├── api
