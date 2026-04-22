package com.commerce.ingestion_service.controller;

import com.commerce.ingestion.api.FulfillmentsApi;
import com.commerce.ingestion.dto.*;
import com.commerce.ingestion_service.domain.Fulfillment;
import com.commerce.ingestion_service.mapper.FulfillmentMapper;
import com.commerce.ingestion_service.service.FulfillmentService;
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
public class FulfillmentController implements FulfillmentsApi {

    private final FulfillmentService fulfillmentService;
    private final FulfillmentMapper fulfillmentMapper;

    @Override
    public ResponseEntity<com.commerce.ingestion.dto.Fulfillment> createFulfillment(
            UUID orderId, FulfillmentCreateRequest fulfillmentCreateRequest) {
        Fulfillment created = fulfillmentService.createFulfillment(orderId, fulfillmentCreateRequest);
        return new ResponseEntity<>(fulfillmentMapper.toDto(created), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<com.commerce.ingestion.dto.Fulfillment> getFulfillmentById(UUID orderId, UUID fulfillmentId) {
        return fulfillmentService.getFulfillmentById(fulfillmentId)
                .map(f -> new ResponseEntity<>(fulfillmentMapper.toDto(f), HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Override
    public ResponseEntity<PagedFulfillment> listFulfillments(
            UUID orderId, OffsetDateTime from, OffsetDateTime to,
            Integer page, Integer size, String sort,
            FulfillmentStatus status, String carrier) {

        Sort sortObj = Sort.unsorted();
        if (sort != null && !sort.isBlank()) {
            String[] parts = sort.split(",");
            String property = parts[0].trim();
            Sort.Direction direction = parts.length > 1 && "asc".equalsIgnoreCase(parts[1].trim())
                    ? Sort.Direction.ASC : Sort.Direction.DESC;
            sortObj = Sort.by(direction, property);
        }

        Pageable pageable = PageRequest.of(page != null ? page : 0, size != null ? size : 50, sortObj);
        Page<Fulfillment> pageResult = fulfillmentService.listFulfillments(pageable);

        List<com.commerce.ingestion.dto.Fulfillment> dtos = pageResult.getContent().stream()
                .map(fulfillmentMapper::toDto)
                .collect(Collectors.toList());

        PagedFulfillment response = new PagedFulfillment();
        response.setData(dtos);
        response.setPage(pageResult.getNumber());
        response.setSize(pageResult.getSize());
        response.setTotalElements((int) pageResult.getTotalElements());
        response.setTotalPages(pageResult.getTotalPages());
        response.setHasNext(pageResult.hasNext());

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<com.commerce.ingestion.dto.Fulfillment> updateFulfillment(
            UUID orderId, UUID fulfillmentId, FulfillmentUpdateRequest fulfillmentUpdateRequest) {
        try {
            Fulfillment updated = fulfillmentService.updateFulfillment(fulfillmentId, fulfillmentUpdateRequest);
            return ResponseEntity.ok(fulfillmentMapper.toDto(updated));
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<com.commerce.ingestion.dto.Fulfillment> patchFulfillment(
            UUID orderId, UUID fulfillmentId, FulfillmentPatchRequest fulfillmentPatchRequest) {
        try {
            Fulfillment patched = fulfillmentService.patchFulfillment(fulfillmentId, fulfillmentPatchRequest);
            return ResponseEntity.ok(fulfillmentMapper.toDto(patched));
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<Void> deleteFulfillment(UUID orderId, UUID fulfillmentId) {
        try {
            fulfillmentService.deleteFulfillment(fulfillmentId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<PagedFulfillment> searchFulfillmentsByExternal(
            UUID orderId, String externalFulfillmentId, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page != null ? page : 0, size != null ? size : 50);
        Page<Fulfillment> pageResult = fulfillmentService.listFulfillments(pageable);

        List<com.commerce.ingestion.dto.Fulfillment> dtos = pageResult.getContent().stream()
                .filter(f -> externalFulfillmentId.equals(f.getExternalFulfillmentId()))
                .map(fulfillmentMapper::toDto)
                .collect(Collectors.toList());

        PagedFulfillment response = new PagedFulfillment();
        response.setData(dtos);
        response.setPage(pageResult.getNumber());
        response.setSize(pageResult.getSize());
        response.setTotalElements(dtos.size());
        response.setTotalPages(1);
        response.setHasNext(false);

        return ResponseEntity.ok(response);
    }
}
