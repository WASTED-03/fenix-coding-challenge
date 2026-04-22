package com.commerce.ingestion_service.controller;

import com.commerce.ingestion.api.OrganizationsApi;
import com.commerce.ingestion.dto.*;
import com.commerce.ingestion_service.domain.Organization;
import com.commerce.ingestion_service.mapper.OrganizationMapper;
import com.commerce.ingestion_service.service.OrganizationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class OrganizationController implements OrganizationsApi {

    private final OrganizationService organizationService;
    private final OrganizationMapper organizationMapper;

    @Override
    public ResponseEntity<com.commerce.ingestion.dto.Organization> createOrganization(
            OrganizationCreateRequest organizationCreateRequest) {
        Organization created = organizationService.createOrganization(organizationCreateRequest);
        return new ResponseEntity<>(organizationMapper.toDto(created), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<com.commerce.ingestion.dto.Organization> getOrganizationById(UUID id) {
        return organizationService.getOrganizationById(id)
                .map(org -> new ResponseEntity<>(organizationMapper.toDto(org), HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Override
    public ResponseEntity<PagedOrganization> listOrganizations(
            OffsetDateTime from, OffsetDateTime to,
            Integer page, Integer size, String sort,
            OrgStatus status, String name) {
        
        Sort sortObj = Sort.unsorted();
        if (sort != null && !sort.isBlank()) {
            String[] parts = sort.split(",");
            String property = parts[0].trim();
            Sort.Direction direction = parts.length > 1 && "asc".equalsIgnoreCase(parts[1].trim())
                    ? Sort.Direction.ASC : Sort.Direction.DESC;
            sortObj = Sort.by(direction, property);
        }

        Pageable pageable = PageRequest.of(page != null ? page : 0, size != null ? size : 50, sortObj);
        Page<Organization> pageResult = organizationService.listOrganizations(pageable);

        List<com.commerce.ingestion.dto.Organization> dtos = pageResult.getContent().stream()
                .map(organizationMapper::toDto)
                .collect(Collectors.toList());

        PagedOrganization response = new PagedOrganization();
        response.setData(dtos);
        response.setPage(pageResult.getNumber());
        response.setSize(pageResult.getSize());
        response.setTotalElements((int) pageResult.getTotalElements());
        response.setTotalPages(pageResult.getTotalPages());
        response.setHasNext(pageResult.hasNext());

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<com.commerce.ingestion.dto.Organization> updateOrganization(
            UUID id, OrganizationUpdateRequest organizationUpdateRequest) {
        try {
            Organization updated = organizationService.updateOrganization(id, organizationUpdateRequest);
            return ResponseEntity.ok(organizationMapper.toDto(updated));
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<com.commerce.ingestion.dto.Organization> patchOrganization(
            UUID id, OrganizationPatchRequest organizationPatchRequest) {
        try {
            Organization patched = organizationService.patchOrganization(id, organizationPatchRequest);
            return ResponseEntity.ok(organizationMapper.toDto(patched));
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<Void> deleteOrganization(UUID id) {
        try {
            organizationService.deleteOrganization(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<PagedOrganization> searchOrganizationsByRef(
            String externalId, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page != null ? page : 0, size != null ? size : 50);
        Page<Organization> pageResult = organizationService.listOrganizations(pageable);

        List<com.commerce.ingestion.dto.Organization> dtos = pageResult.getContent().stream()
                .filter(org -> externalId.equals(org.getExternalId()))
                .map(organizationMapper::toDto)
                .collect(Collectors.toList());

        PagedOrganization response = new PagedOrganization();
        response.setData(dtos);
        response.setPage(pageResult.getNumber());
        response.setSize(pageResult.getSize());
        response.setTotalElements(dtos.size());
        response.setTotalPages(1);
        response.setHasNext(false);

        return ResponseEntity.ok(response);
    }
}
