package com.commerce.ingestion_service;

import com.commerce.ingestion.dto.OrderCreateRequest;
import com.commerce.ingestion.dto.OrgStatus;
import com.commerce.ingestion.dto.Platform;
import com.commerce.ingestion.dto.WebsiteStatus;
import com.commerce.ingestion_service.config.TenantInterceptor;
import com.commerce.ingestion_service.domain.Organization;
import com.commerce.ingestion_service.domain.Website;
import com.commerce.ingestion_service.repository.OrganizationRepository;
import com.commerce.ingestion_service.repository.WebsiteRepository;
import com.commerce.ingestion_service.tenant.TenantContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class IngestionApiIntegrationTest {

    @Container
    @ServiceConnection
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0");

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private WebsiteRepository websiteRepository;

    private UUID tenantA;
    private UUID tenantB;
    private UUID websiteA;

    @BeforeEach
    void setUp() {
        // Create Tenant A
        TenantContext.setTenantId(null);
        Organization orgA = new Organization();
        orgA.setName("Tenant A");
        orgA.setStatus(OrgStatus.ACTIVE);
        orgA = organizationRepository.save(orgA);
        tenantA = orgA.getId();

        // Create Website for Tenant A
        TenantContext.setTenantId(tenantA);
        Website webA = new Website();
        webA.setCode("TENA-WEB");
        webA.setName("Tenant A Website");
        webA.setPlatform(Platform.SHOPIFY);
        webA.setDomain("a.com");
        webA.setStatus(WebsiteStatus.ACTIVE);
        webA = websiteRepository.save(webA);
        websiteA = webA.getId();

        // Create Tenant B
        Organization orgB = new Organization();
        orgB.setName("Tenant B");
        orgB.setStatus(OrgStatus.ACTIVE);
        orgB = organizationRepository.save(orgB);
        tenantB = orgB.getId();
        
        // Override the mock UUIDs for testing to match what the DB generated
        TenantInterceptor.setTestMap(Map.of(
            "admin@acme.com", tenantA,
            "admin@globex.com", tenantB
        ));
    }

    @Test
    void shouldEnforceStrictDataIsolationBetweenTenants() {
        // 1. Create an Order for Tenant A
        HttpHeaders headersA = new HttpHeaders();
        headersA.setBasicAuth("admin@acme.com", "password123");
        headersA.setContentType(MediaType.APPLICATION_JSON);

        OrderCreateRequest request = new OrderCreateRequest();
        request.setOrgId(tenantA);
        request.setWebsiteId(websiteA);
        request.setExternalOrderId("EXT-123");
        request.setStatus(com.commerce.ingestion.dto.OrderStatus.CREATED);
        request.setFinancialStatus(com.commerce.ingestion.dto.FinancialStatus.PAID);
        request.setFulfillmentStatus(com.commerce.ingestion.dto.FulfillmentOverallStatus.UNFULFILLED);
        request.setOrderTotal(100.0);
        request.setOrderCreatedAt(JsonNullable.of(OffsetDateTime.now()));

        ResponseEntity<String> createResponse = restTemplate.exchange(
                "/orders",
                HttpMethod.POST,
                new HttpEntity<>(request, headersA),
                String.class
        );

        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        // Extract createdOrderId from response, assuming it has "id":"uuid" format.
        // For simplicity we parse the string or just use Regex
        String body = createResponse.getBody();
        assertNotNull(body);

        String idKey = "\"id\":\"";
        int startId = body.indexOf(idKey) + idKey.length();
        int endId = body.indexOf("\"", startId);
        String createdOrderId = body.substring(startId, endId);

        // 2. Attempt to GET that same Order ID using Tenant B's Header
        HttpHeaders headersB = new HttpHeaders();
        headersB.setBasicAuth("admin@globex.com", "password456");

        ResponseEntity<String> response = restTemplate.exchange(
                "/orders/" + createdOrderId,
                HttpMethod.GET,
                new HttpEntity<>(headersB),
                String.class
        );

        // 3. ASSERT: Tenant B should NOT see Tenant A's data
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void shouldFailValidationWhenCreatingOrderForOtherTenantsWebsite() {
        // Here we define Tenant B making the request but attempting to use Tenant A's Website
        HttpHeaders headersB = new HttpHeaders();
        headersB.setBasicAuth("admin@globex.com", "password456");
        headersB.setContentType(MediaType.APPLICATION_JSON);

        OrderCreateRequest request = new OrderCreateRequest();
        request.setOrgId(tenantB);
        request.setWebsiteId(websiteA); // websiteA belongs to tenantA!
        request.setExternalOrderId("EXT-MALICIOUS");
        request.setStatus(com.commerce.ingestion.dto.OrderStatus.CREATED);
        request.setFinancialStatus(com.commerce.ingestion.dto.FinancialStatus.PAID);
        request.setFulfillmentStatus(com.commerce.ingestion.dto.FulfillmentOverallStatus.UNFULFILLED);
        request.setOrderTotal(100.0);
        request.setOrderCreatedAt(JsonNullable.of(OffsetDateTime.now()));

        ResponseEntity<String> createResponse = restTemplate.exchange(
                "/orders",
                HttpMethod.POST,
                new HttpEntity<>(request, headersB),
                String.class
        );

        // This should return 404 Not Found since websiteA shouldn't be found for tenantB
        assertEquals(HttpStatus.NOT_FOUND, createResponse.getStatusCode());
    }
}
