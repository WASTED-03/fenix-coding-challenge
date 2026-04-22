package com.commerce.ingestion_service.domain;

import com.commerce.ingestion.dto.FinancialStatus;
import com.commerce.ingestion.dto.FulfillmentOverallStatus;
import com.commerce.ingestion.dto.OrderStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class Order extends AbstractTenantEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "website_id", nullable = false)
    private UUID websiteId;

    @Column(name = "external_order_id", nullable = false)
    private String externalOrderId;

    @Column(name = "external_order_number")
    private String externalOrderNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "financial_status", nullable = false)
    private FinancialStatus financialStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "fulfillment_status", nullable = false)
    private FulfillmentOverallStatus fulfillmentStatus;

    @Column(name = "customer_email")
    private String customerEmail;

    @Column(name = "order_total", nullable = false)
    private Double orderTotal;

    @Column
    private String currency;

    @Column(name = "order_created_at")
    private LocalDateTime orderCreatedAt;

    @Column(name = "order_updated_at")
    private LocalDateTime orderUpdatedAt;

    @Column(name = "ingested_at", nullable = false)
    private LocalDateTime ingestedAt;
}
