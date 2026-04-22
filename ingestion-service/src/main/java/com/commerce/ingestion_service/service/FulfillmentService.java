package com.commerce.ingestion_service.service;

import com.commerce.ingestion.dto.FulfillmentCreateRequest;
import com.commerce.ingestion.dto.FulfillmentPatchRequest;
import com.commerce.ingestion.dto.FulfillmentUpdateRequest;
import com.commerce.ingestion_service.domain.Fulfillment;
import com.commerce.ingestion_service.mapper.FulfillmentMapper;
import com.commerce.ingestion_service.repository.FulfillmentRepository;
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
public class FulfillmentService {

    private final FulfillmentRepository fulfillmentRepository;
    private final FulfillmentMapper fulfillmentMapper;

    @Transactional
    public Fulfillment createFulfillment(UUID orderId, FulfillmentCreateRequest request) {
        Fulfillment fulfillment = fulfillmentMapper.toEntity(request, orderId);
        return fulfillmentRepository.save(fulfillment);
    }

    @Transactional(readOnly = true)
    public Optional<Fulfillment> getFulfillmentById(UUID id) {
        return fulfillmentRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Page<Fulfillment> listFulfillments(Pageable pageable) {
        return fulfillmentRepository.findAll(pageable);
    }

    @Transactional
    public Fulfillment updateFulfillment(UUID id, FulfillmentUpdateRequest request) {
        Fulfillment fulfillment = fulfillmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Fulfillment not found with id: " + id));
        fulfillmentMapper.updateEntity(fulfillment, request);
        return fulfillmentRepository.save(fulfillment);
    }

    @Transactional
    public Fulfillment patchFulfillment(UUID id, FulfillmentPatchRequest request) {
        Fulfillment fulfillment = fulfillmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Fulfillment not found with id: " + id));
        fulfillmentMapper.patchEntity(fulfillment, request);
        return fulfillmentRepository.save(fulfillment);
    }

    @Transactional
    public void deleteFulfillment(UUID id) {
        if (!fulfillmentRepository.existsById(id)) {
            throw new EntityNotFoundException("Fulfillment not found with id: " + id);
        }
        fulfillmentRepository.deleteById(id);
    }
}
