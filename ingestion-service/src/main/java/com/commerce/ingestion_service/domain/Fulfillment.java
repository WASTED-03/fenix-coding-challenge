package com.commerce.ingestion_service.domain;

import com.commerce.ingestion.dto.FulfillmentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "fulfillments")
@Getter
@Setter
public class Fulfillment extends AbstractTenantEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(name = "external_fulfillment_id", nullable = false)
    private String externalFulfillmentId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FulfillmentStatus status;

    @Column
    private String carrier;

    @Column(name = "service_level")
    private String serviceLevel;

    @Column(name = "shipped_at")
    private LocalDateTime shippedAt;

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;
}
