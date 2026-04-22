package com.commerce.ingestion_service.repository;

import com.commerce.ingestion_service.domain.Fulfillment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FulfillmentRepository extends JpaRepository<Fulfillment, UUID> {
    Optional<Fulfillment> findByIdAndOrganizationId(UUID id, UUID organizationId);
}
