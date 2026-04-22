package com.commerce.ingestion_service.mapper;

import com.commerce.ingestion.dto.WebsiteCreateRequest;
import com.commerce.ingestion.dto.WebsitePatchRequest;
import com.commerce.ingestion.dto.WebsiteStatus;
import com.commerce.ingestion.dto.WebsiteUpdateRequest;
import com.commerce.ingestion_service.domain.Website;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;

@Component
public class WebsiteMapper {

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

    public Website toEntity(WebsiteCreateRequest request) {
        Website website = new Website();
        website.setCode(request.getCode());
        website.setName(request.getName());
        website.setPlatform(request.getPlatform());
        website.setDomain(unwrap(request.getDomain()));
        if (request.getStatus() != null) {
            website.setStatus(request.getStatus());
        } else {
            website.setStatus(WebsiteStatus.ACTIVE);
        }
        return website;
    }

    public void updateEntity(Website website, WebsiteUpdateRequest request) {
        website.setCode(request.getCode());
        website.setName(request.getName());
        website.setPlatform(request.getPlatform());
        website.setDomain(unwrap(request.getDomain()));
        website.setStatus(request.getStatus());
    }

    public void patchEntity(Website website, WebsitePatchRequest request) {
        if (request.getCode() != null) {
            website.setCode(request.getCode());
        }
        if (request.getName() != null) {
            website.setName(request.getName());
        }
        if (request.getPlatform() != null) {
            website.setPlatform(request.getPlatform());
        }
        JsonNullable<String> domain = request.getDomain();
        if (domain != null && domain.isPresent()) {
            website.setDomain(domain.get());
        }
        if (request.getStatus() != null) {
            website.setStatus(request.getStatus());
        }
    }

    public com.commerce.ingestion.dto.Website toDto(Website entity) {
        com.commerce.ingestion.dto.Website dto = new com.commerce.ingestion.dto.Website();
        dto.setId(entity.getId());
        dto.setOrgId(entity.getOrganizationId());
        dto.setCode(entity.getCode());
        dto.setName(entity.getName());
        dto.setPlatform(entity.getPlatform());
        dto.setDomain(wrap(entity.getDomain()));
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
