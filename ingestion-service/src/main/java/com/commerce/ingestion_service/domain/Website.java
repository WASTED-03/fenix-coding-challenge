package com.commerce.ingestion_service.domain;

import com.commerce.ingestion.dto.Platform;
import com.commerce.ingestion.dto.WebsiteStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "websites")
@Getter
@Setter
public class Website extends AbstractTenantEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Platform platform;

    @Column
    private String domain;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WebsiteStatus status;
}
