package com.commerce.ingestion_service.mapper;

import com.commerce.ingestion.dto.FulfillmentCreateRequest;
import com.commerce.ingestion.dto.FulfillmentPatchRequest;
import com.commerce.ingestion.dto.FulfillmentStatus;
import com.commerce.ingestion.dto.FulfillmentUpdateRequest;
import com.commerce.ingestion_service.domain.Fulfillment;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Component
public class FulfillmentMapper {

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

    public Fulfillment toEntity(FulfillmentCreateRequest request, UUID orderId) {
        Fulfillment fulfillment = new Fulfillment();
        fulfillment.setOrderId(orderId);
        fulfillment.setExternalFulfillmentId(request.getExternalFulfillmentId());
        if (request.getStatus() != null) {
            fulfillment.setStatus(request.getStatus());
        } else {
            fulfillment.setStatus(FulfillmentStatus.CREATED);
        }
        fulfillment.setCarrier(unwrap(request.getCarrier()));
        fulfillment.setServiceLevel(unwrap(request.getServiceLevel()));

        OffsetDateTime shippedAt = unwrap(request.getShippedAt());
        if (shippedAt != null) {
            fulfillment.setShippedAt(shippedAt.toLocalDateTime());
        }

        OffsetDateTime deliveredAt = unwrap(request.getDeliveredAt());
        if (deliveredAt != null) {
            fulfillment.setDeliveredAt(deliveredAt.toLocalDateTime());
        }

        return fulfillment;
    }

    public void updateEntity(Fulfillment fulfillment, FulfillmentUpdateRequest request) {
        fulfillment.setExternalFulfillmentId(request.getExternalFulfillmentId());
        if (request.getStatus() != null) {
            fulfillment.setStatus(request.getStatus());
        }
        fulfillment.setCarrier(unwrap(request.getCarrier()));
        fulfillment.setServiceLevel(unwrap(request.getServiceLevel()));

        OffsetDateTime shippedAt = unwrap(request.getShippedAt());
        fulfillment.setShippedAt(shippedAt != null ? shippedAt.toLocalDateTime() : null);

        OffsetDateTime deliveredAt = unwrap(request.getDeliveredAt());
        fulfillment.setDeliveredAt(deliveredAt != null ? deliveredAt.toLocalDateTime() : null);
    }

    public void patchEntity(Fulfillment fulfillment, FulfillmentPatchRequest request) {
        if (request.getStatus() != null) {
            fulfillment.setStatus(request.getStatus());
        }
        JsonNullable<String> carrier = request.getCarrier();
        if (carrier != null && carrier.isPresent()) {
            fulfillment.setCarrier(carrier.get());
        }
        JsonNullable<String> serviceLevel = request.getServiceLevel();
        if (serviceLevel != null && serviceLevel.isPresent()) {
            fulfillment.setServiceLevel(serviceLevel.get());
        }
        JsonNullable<OffsetDateTime> shippedAt = request.getShippedAt();
        if (shippedAt != null && shippedAt.isPresent()) {
            fulfillment.setShippedAt(shippedAt.get().toLocalDateTime());
        }
        JsonNullable<OffsetDateTime> deliveredAt = request.getDeliveredAt();
        if (deliveredAt != null && deliveredAt.isPresent()) {
            fulfillment.setDeliveredAt(deliveredAt.get().toLocalDateTime());
        }
    }

    public com.commerce.ingestion.dto.Fulfillment toDto(Fulfillment entity) {
        com.commerce.ingestion.dto.Fulfillment dto = new com.commerce.ingestion.dto.Fulfillment();
        dto.setId(entity.getId());
        dto.setOrderId(entity.getOrderId());
        dto.setExternalFulfillmentId(entity.getExternalFulfillmentId());
        dto.setStatus(entity.getStatus());
        dto.setCarrier(wrap(entity.getCarrier()));
        dto.setServiceLevel(wrap(entity.getServiceLevel()));
        if (entity.getShippedAt() != null) {
            dto.setShippedAt(wrap(entity.getShippedAt().atOffset(ZoneOffset.UTC)));
        }
        if (entity.getDeliveredAt() != null) {
            dto.setDeliveredAt(wrap(entity.getDeliveredAt().atOffset(ZoneOffset.UTC)));
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
