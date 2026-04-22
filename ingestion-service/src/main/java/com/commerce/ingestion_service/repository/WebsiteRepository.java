package com.commerce.ingestion_service.repository;

import com.commerce.ingestion_service.domain.Website;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface WebsiteRepository extends JpaRepository<Website, UUID> {
}
