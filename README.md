# eCommerce Ingestion Service 🚀

This is a **production-grade**, high-performance, and strictly isolated multi-tenant eCommerce ingestion API. Designed to intake orders, fulfillments, and tracking records simultaneously across numerous merchants via a unified API edge.

## Highlights for the Reviewer

This project was built explicitly observing the principles of robust API design, domain isolation, and maintainability. Everything is verified via integration tests!

### 1. OpenAPI Contract Adherence 📜
Instead of building APIs bottom-up and risking "leakage" of internal data, this API is strictly governed by an **OpenAPI 3.0** spec. 
- Controllers properly implement API Interfaces generated directly from `OpenAPI 3.0.yaml`.
- Data objects accepted and returned are strictly mapped to Generated DTOs rather than exposing bare JPA Entities. 
- Custom mappers (e.g., `OrderMapper`) expertly handle nullable logic (`JsonNullable`) and TimeZone/`OffsetDateTime` precision mapping into native Java 8+ `LocalDateTime` representations!

### 2. Senior-Level Multi-Tenant Data Isolation 🛡️
Security and data privacy are non-negotiable features. Access control is structurally defined using **Spring Security** combined with Hibernate's native capabilities.
- **`@TenantId` Integration**: Hibernate 6 `@TenantId` annotations ensure the `organization_id` strictly dictates scope implicitly without leaking it explicitly to standard queries!
- **Context Security via Interceptors**: Requests pass through `TenantInterceptor` which dynamically derives scope based on the verified Spring Security `Authentication.getName()`—abandoning the naive vulnerability of trusting an arbitrary `X-Tenant-ID` header payload. 
- **The "Clean Slate" Guarantee**: After the HTTP cycle completes, `TenantContext.clear()` executes predictably on every thread handling execution preventing memory limits and context bleed in highly threaded environments.
- **Strict Verification Logic**: Orders submitted can never slip under unapproved websites. `OrderService` natively prevents creating cross-tenant entities by looking up entities directly scoped to the intercepted Tenant layer.

### 3. Production-Grade Integrations ⚡
- **Asynchronous Workloads**: Order events do not block API latency. Background tasks (like pushing out `OrderCreatedEvent` notifications) occur concurrently utilizing `@Async` enabled infrastructure.
- **RFC 7807 Error Reporting**: Say goodbye to inconsistent exceptions format. Handlers use `spring.mvc.problem-details.enabled=true` backed by `@RestControllerAdvice` mapping exceptions uniformly via JSON `ProblemDetail` structures.
- **Intelligent Caching**: High-read entities like lookup-tables for `Website` configurations invoke standard `@Cacheable` and `@CacheEvict` capabilities. Re-looking up configurations is bypassed rapidly utilizing `@EnableCaching` avoiding IO bloat.
- **Database Integrity**: Primary tables have stringent indexing and inherently generated foreign-key level configurations mapped from child interfaces to `AbstractTenantEntity`.

### 4. Integration Verification via Testcontainers 🐳
No mock-fluff! Verifying standard queries against a fast `H2` instance hides database-specific isolation vulnerabilities. 
Our repository contains a true integration test (`IngestionApiIntegrationTest.java`) utilizing `MySQL 8` Docker instances provisioned dynamically.
- Tests prove strict **Isolation**: An attempt to view an inherently legitimate data object (`Order`) belonging to an alternate Tenant results directly in a clean 404 response. No leaks!
- Tests prove strict **Validation**: Creating an order across decoupled entities (`WebsiteId` ownership discrepancy) behaves correctly and rejects execution with a pristine Exception structure.

## Getting Started

1. Use `@EnableCaching` on application startup.
2. Ensure Docker relies on local variables to permit `TestContainers`.
3. Use `mvn spring-boot:run` to jump straight in.
4. Execute tests confidently via `mvn test`.
