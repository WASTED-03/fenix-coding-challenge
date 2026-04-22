# Multi-tenant Commerce Ingestion API

A production-grade Java Spring Boot ingestion layer designed to process orders and fulfillments across multiple organizations (tenants) with strict data isolation.

##  Architectural Highlights

* **API-First Design**: 100% compliance with the OpenAPI 3.0 specification.
* **Strict Multi-Tenancy**: Implemented using **Hibernate 6 `@TenantId`**. Data is mathematically isolated at the database level, ensuring Organization A can never access Organization B's data.
* **Identity-Bound Isolation**: Tenant IDs are derived from the authenticated **Spring Security** Principal, preventing "Tenant Spoofing."
* **Event-Driven Ingestion**: Uses `@Async` and `ApplicationEventPublisher` to process order data in the background, maintaining low-latency API responses.
* **Performance Optimization**: Integrated **Spring Cache** to reduce database IOPS for read-heavy website and organization lookups.
* **Professional Error Handling**: Implements **RFC 7807 Problem Details** for standardized JSON error responses.

##  Tech Stack

* **Java 17** & **Spring Boot 3.x**
* **Spring Data JPA** & **Hibernate 6**
* **MySQL 8**
* **Testcontainers** (for integration testing)
* **Spring Security** (Basic Auth)

##  Getting Started

### Prerequisites
* Docker Desktop (Required for MySQL and Testcontainers)
* Java 17+

### Running the App
1. Start the database:
   ```bash
   docker-compose up -d
Build and run:

Bash
mvn clean spring-boot:run
Running Tests
To verify the multi-tenant isolation logic against a real MySQL instance:

Bash
mvn test
 Test Credentials-
Use the following credentials for API testing:

  •Username: admin@acme.com

  •Password: password123

  •Tenant Header: X-Tenant-ID: [Organization-UUID]
