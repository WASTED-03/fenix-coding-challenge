package com.commerce.ingestion_service.domain;

import com.commerce.ingestion.dto.TrackingStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "trackings")
@Getter
@Setter
public class Tracking extends AbstractTenantEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "fulfillment_id", nullable = false)
    private UUID fulfillmentId;

    @Column(name = "tracking_number", nullable = false)
    private String trackingNumber;

    @Column
    private String carrier;

    @Column(name = "tracking_url")
    private String trackingUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TrackingStatus status;

    @Column(name = "is_primary", nullable = false)
    private Boolean isPrimary = false;

    @Column(name = "last_event_at")
    private LocalDateTime lastEventAt;
}
