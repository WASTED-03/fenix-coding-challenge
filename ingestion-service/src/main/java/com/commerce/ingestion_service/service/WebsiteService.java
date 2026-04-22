package com.commerce.ingestion_service.service;

import com.commerce.ingestion.dto.WebsiteCreateRequest;
import com.commerce.ingestion.dto.WebsitePatchRequest;
import com.commerce.ingestion.dto.WebsiteUpdateRequest;
import com.commerce.ingestion_service.domain.Website;
import com.commerce.ingestion_service.mapper.WebsiteMapper;
import com.commerce.ingestion_service.repository.WebsiteRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WebsiteService {

    private final WebsiteRepository websiteRepository;
    private final WebsiteMapper websiteMapper;

    @Transactional
    public Website createWebsite(WebsiteCreateRequest request) {
        Website website = websiteMapper.toEntity(request);
        return websiteRepository.save(website);
    }

    @Cacheable(value = "websites", key = "#id")
    public Website getWebsiteById(UUID id) {
        return websiteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Website not found"));
    }

    @Transactional(readOnly = true)
    public Optional<Website> findWebsiteById(UUID id) {
        return websiteRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Page<Website> listWebsites(Pageable pageable) {
        return websiteRepository.findAll(pageable);
    }

    @Transactional
    @CacheEvict(value = "websites", key = "#id")
    public Website updateWebsiteFull(UUID id, WebsiteUpdateRequest request) {
        Website website = websiteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Website not found with id: " + id));
        websiteMapper.updateEntity(website, request);
        return websiteRepository.save(website);
    }

    @Transactional
    @CacheEvict(value = "websites", key = "#id")
    public Website patchWebsite(UUID id, WebsitePatchRequest request) {
        Website website = websiteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Website not found with id: " + id));
        websiteMapper.patchEntity(website, request);
        return websiteRepository.save(website);
    }

    @Transactional
    @CacheEvict(value = "websites", key = "#id")
    public void updateWebsite(UUID id, WebsiteUpdateRequest request) {
        Website website = getWebsiteById(id);
        if (request.getName() != null) {
            website.setName(request.getName());
        }
        if (request.getCode() != null) {
            website.setCode(request.getCode());
        }
        if (request.getPlatform() != null) {
            website.setPlatform(request.getPlatform());
        }
        if (request.getStatus() != null) {
            website.setStatus(request.getStatus());
        }
        websiteRepository.save(website);
    }

    @Transactional
    @CacheEvict(value = "websites", key = "#id")
    public void deleteWebsite(UUID id) {
        if (!websiteRepository.existsById(id)) {
            throw new EntityNotFoundException("Website not found with id: " + id);
        }
        websiteRepository.deleteById(id);
    }
}
