package com.commerce.ingestion_service.repository;

import com.commerce.ingestion_service.domain.Tracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TrackingRepository extends JpaRepository<Tracking, UUID> {
    Optional<Tracking> findByIdAndOrganizationId(UUID id, UUID organizationId);
}
