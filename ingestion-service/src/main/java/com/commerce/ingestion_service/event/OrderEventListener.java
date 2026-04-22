package com.commerce.ingestion_service.event;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class OrderEventListener {

    @Async
    @EventListener
    public void handleOrderCreated(OrderCreatedEvent event) {
        System.out.println("Processing order ingestion for external systems: " + event.order().getExternalOrderId());
    }
}
