package com.commerce.ingestion_service.repository;

import com.commerce.ingestion_service.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    Optional<Order> findByIdAndOrganizationId(UUID id, UUID organizationId);
}
