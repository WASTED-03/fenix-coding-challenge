package com.commerce.ingestion_service.mapper;

import com.commerce.ingestion.dto.OrderCreateRequest;
import com.commerce.ingestion_service.domain.Order;
import com.commerce.ingestion_service.domain.Website;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Component
public class OrderMapper {

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

    public Order toEntity(OrderCreateRequest request, Website website) {
        Order order = new Order();
        order.setWebsiteId(website.getId());
        order.setExternalOrderId(request.getExternalOrderId());
        
        order.setExternalOrderNumber(unwrap(request.getExternalOrderNumber()));
        order.setStatus(request.getStatus());
        order.setFinancialStatus(request.getFinancialStatus());
        order.setFulfillmentStatus(request.getFulfillmentStatus());
        order.setCustomerEmail(unwrap(request.getCustomerEmail()));
        order.setOrderTotal(request.getOrderTotal());
        order.setCurrency(unwrap(request.getCurrency()));
        
        java.time.OffsetDateTime orderCreatedAt = unwrap(request.getOrderCreatedAt());
        if (orderCreatedAt != null) {
            order.setOrderCreatedAt(orderCreatedAt.toLocalDateTime());
        }
        
        java.time.OffsetDateTime orderUpdatedAt = unwrap(request.getOrderUpdatedAt());
        if (orderUpdatedAt != null) {
            order.setOrderUpdatedAt(orderUpdatedAt.toLocalDateTime());
        }
        
        order.setIngestedAt(LocalDateTime.now());
        return order;
    }

    public com.commerce.ingestion.dto.Order toDto(Order entity) {
        com.commerce.ingestion.dto.Order dto = new com.commerce.ingestion.dto.Order();
        dto.setId(entity.getId());
        dto.setOrgId(entity.getOrganizationId());
        dto.setWebsiteId(entity.getWebsiteId());
        dto.setExternalOrderId(entity.getExternalOrderId());
        
        dto.setExternalOrderNumber(wrap(entity.getExternalOrderNumber()));
        dto.setStatus(entity.getStatus());
        dto.setFinancialStatus(entity.getFinancialStatus());
        dto.setFulfillmentStatus(entity.getFulfillmentStatus());
        dto.setCustomerEmail(wrap(entity.getCustomerEmail()));
        dto.setOrderTotal(entity.getOrderTotal());
        dto.setCurrency(wrap(entity.getCurrency()));

        if (entity.getOrderCreatedAt() != null) {
            dto.setOrderCreatedAt(wrap(entity.getOrderCreatedAt().atOffset(ZoneOffset.UTC)));
        }
        if (entity.getOrderUpdatedAt() != null) {
            dto.setOrderUpdatedAt(wrap(entity.getOrderUpdatedAt().atOffset(ZoneOffset.UTC)));
        }
        if (entity.getIngestedAt() != null) {
            dto.setIngestedAt(entity.getIngestedAt().atOffset(ZoneOffset.UTC));
        }
        if (entity.getCreatedAt() != null) {
            dto.setCreatedAt(entity.getCreatedAt().atOffset(ZoneOffset.UTC));
        }
        if (entity.getUpdatedAt() != null) {
            dto.setUpdatedAt(entity.getUpdatedAt().atOffset(ZoneOffset.UTC));
        }
        return dto;
    }
}
