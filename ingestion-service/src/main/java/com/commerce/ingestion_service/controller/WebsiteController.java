package com.commerce.ingestion_service.controller;

import com.commerce.ingestion.api.WebsitesApi;
import com.commerce.ingestion.dto.*;
import com.commerce.ingestion_service.domain.Website;
import com.commerce.ingestion_service.mapper.WebsiteMapper;
import com.commerce.ingestion_service.service.WebsiteService;
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
public class WebsiteController implements WebsitesApi {

    private final WebsiteService websiteService;
    private final WebsiteMapper websiteMapper;

    @Override
    public ResponseEntity<com.commerce.ingestion.dto.Website> createWebsite(
            UUID orgId, WebsiteCreateRequest websiteCreateRequest) {
        Website created = websiteService.createWebsite(websiteCreateRequest);
        return new ResponseEntity<>(websiteMapper.toDto(created), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<com.commerce.ingestion.dto.Website> getWebsiteById(UUID orgId, UUID websiteId) {
        return websiteService.findWebsiteById(websiteId)
                .map(website -> new ResponseEntity<>(websiteMapper.toDto(website), HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Override
    public ResponseEntity<PagedWebsite> listWebsites(
            UUID orgId, OffsetDateTime from, OffsetDateTime to,
            Integer page, Integer size, String sort,
            WebsiteStatus status, Platform platform, String code, String domain) {

        Sort sortObj = Sort.unsorted();
        if (sort != null && !sort.isBlank()) {
            String[] parts = sort.split(",");
            String property = parts[0].trim();
            Sort.Direction direction = parts.length > 1 && "asc".equalsIgnoreCase(parts[1].trim())
                    ? Sort.Direction.ASC : Sort.Direction.DESC;
            sortObj = Sort.by(direction, property);
        }

        Pageable pageable = PageRequest.of(page != null ? page : 0, size != null ? size : 50, sortObj);
        Page<Website> pageResult = websiteService.listWebsites(pageable);

        List<com.commerce.ingestion.dto.Website> dtos = pageResult.getContent().stream()
                .map(websiteMapper::toDto)
                .collect(Collectors.toList());

        PagedWebsite response = new PagedWebsite();
        response.setData(dtos);
        response.setPage(pageResult.getNumber());
        response.setSize(pageResult.getSize());
        response.setTotalElements((int) pageResult.getTotalElements());
        response.setTotalPages(pageResult.getTotalPages());
        response.setHasNext(pageResult.hasNext());

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<com.commerce.ingestion.dto.Website> updateWebsite(
            UUID orgId, UUID websiteId, WebsiteUpdateRequest websiteUpdateRequest) {
        try {
            Website updated = websiteService.updateWebsiteFull(websiteId, websiteUpdateRequest);
            return ResponseEntity.ok(websiteMapper.toDto(updated));
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<com.commerce.ingestion.dto.Website> patchWebsite(
            UUID orgId, UUID websiteId, WebsitePatchRequest websitePatchRequest) {
        try {
            Website patched = websiteService.patchWebsite(websiteId, websitePatchRequest);
            return ResponseEntity.ok(websiteMapper.toDto(patched));
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<Void> deleteWebsite(UUID orgId, UUID websiteId) {
        try {
            websiteService.deleteWebsite(websiteId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<PagedWebsite> searchWebsites(
            UUID orgId, UUID websiteId, String code, String domain,
            Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page != null ? page : 0, size != null ? size : 50);
        Page<Website> pageResult = websiteService.listWebsites(pageable);

        List<com.commerce.ingestion.dto.Website> dtos = pageResult.getContent().stream()
                .map(websiteMapper::toDto)
                .collect(Collectors.toList());

        PagedWebsite response = new PagedWebsite();
        response.setData(dtos);
        response.setPage(pageResult.getNumber());
        response.setSize(pageResult.getSize());
        response.setTotalElements((int) pageResult.getTotalElements());
        response.setTotalPages(pageResult.getTotalPages());
        response.setHasNext(pageResult.hasNext());

        return ResponseEntity.ok(response);
    }
}
