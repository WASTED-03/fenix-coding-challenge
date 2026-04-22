package com.commerce.ingestion_service.mapper;

import com.commerce.ingestion.dto.OrganizationCreateRequest;
import com.commerce.ingestion.dto.OrganizationPatchRequest;
import com.commerce.ingestion.dto.OrganizationUpdateRequest;
import com.commerce.ingestion_service.domain.Organization;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;

@Component
public class OrganizationMapper {

    private <T> T unwrap(JsonNullable<T> nullable) {
        if (nullable == null || !nullable.isPresent()) {
            return null;
        }
        return nullable.get();
    }

    private <T> JsonNullable<T> wrap(T obj) {
        if (obj == null) {
            return JsonNullable.undefined();
        }
        return JsonNullable.of(obj);
    }

    public Organization toEntity(OrganizationCreateRequest request) {
        Organization org = new Organization();
        org.setName(request.getName());
        org.setExternalId(unwrap(request.getExternalId()));
        if (request.getStatus() != null) {
            org.setStatus(request.getStatus());
        } else {
            org.setStatus(com.commerce.ingestion.dto.OrgStatus.ACTIVE);
        }
        return org;
    }

    public void updateEntity(Organization org, OrganizationUpdateRequest request) {
        org.setName(request.getName());
        org.setExternalId(unwrap(request.getExternalId()));
        if (request.getStatus() != null) {
            org.setStatus(request.getStatus());
        }
    }

    public void patchEntity(Organization org, OrganizationPatchRequest request) {
        if (request.getName() != null) {
            org.setName(request.getName());
        }
        JsonNullable<String> externalId = request.getExternalId();
        if (externalId != null && externalId.isPresent()) {
            org.setExternalId(externalId.get());
        }
        if (request.getStatus() != null) {
            org.setStatus(request.getStatus());
        }
    }

    public com.commerce.ingestion.dto.Organization toDto(Organization entity) {
        com.commerce.ingestion.dto.Organization dto = new com.commerce.ingestion.dto.Organization();
        dto.setId(entity.getId());
        dto.setExternalId(wrap(entity.getExternalId()));
        dto.setName(entity.getName());
        dto.setStatus(entity.getStatus());
        if (entity.getCreatedAt() != null) {
            dto.setCreatedAt(entity.getCreatedAt().atOffset(ZoneOffset.UTC));
        }
        if (entity.getUpdatedAt() != null) {
            dto.setUpdatedAt(entity.getUpdatedAt().atOffset(ZoneOffset.UTC));
        }
        return dto;
    }
}
