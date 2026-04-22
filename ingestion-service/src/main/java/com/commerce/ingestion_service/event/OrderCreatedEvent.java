package com.commerce.ingestion_service.event;

import com.commerce.ingestion_service.domain.Order;

public record OrderCreatedEvent(Order order) {}
