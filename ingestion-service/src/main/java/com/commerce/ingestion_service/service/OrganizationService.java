package com.commerce.ingestion_service.service;

import com.commerce.ingestion.dto.OrganizationCreateRequest;
import com.commerce.ingestion.dto.OrganizationPatchRequest;
import com.commerce.ingestion.dto.OrganizationUpdateRequest;
import com.commerce.ingestion_service.domain.Organization;
import com.commerce.ingestion_service.mapper.OrganizationMapper;
import com.commerce.ingestion_service.repository.OrganizationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final OrganizationMapper organizationMapper;

    @Transactional
    public Organization createOrganization(OrganizationCreateRequest request) {
        Organization org = organizationMapper.toEntity(request);
        return organizationRepository.save(org);
    }

    @Transactional(readOnly = true)
    public Optional<Organization> getOrganizationById(UUID id) {
        return organizationRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Page<Organization> listOrganizations(Pageable pageable) {
        return organizationRepository.findAll(pageable);
    }

    @Transactional
    public Organization updateOrganization(UUID id, OrganizationUpdateRequest request) {
        Organization org = organizationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Organization not found with id: " + id));
        organizationMapper.updateEntity(org, request);
        return organizationRepository.save(org);
    }

    @Transactional
    public Organization patchOrganization(UUID id, OrganizationPatchRequest request) {
        Organization org = organizationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Organization not found with id: " + id));
        organizationMapper.patchEntity(org, request);
        return organizationRepository.save(org);
    }

    @Transactional
    public void deleteOrganization(UUID id) {
        if (!organizationRepository.existsById(id)) {
            throw new EntityNotFoundException("Organization not found with id: " + id);
        }
        organizationRepository.deleteById(id);
    }
}
