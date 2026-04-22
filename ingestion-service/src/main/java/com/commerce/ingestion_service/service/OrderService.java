package com.commerce.ingestion_service.service;

import com.commerce.ingestion.dto.OrderCreateRequest;
import com.commerce.ingestion_service.domain.Order;
import com.commerce.ingestion_service.domain.Website;
import com.commerce.ingestion_service.event.OrderCreatedEvent;
import com.commerce.ingestion_service.mapper.OrderMapper;
import com.commerce.ingestion_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebsiteService websiteService;
    private final ApplicationEventPublisher eventPublisher;
    private final OrderMapper orderMapper;

    @Transactional
    public Order createOrUpdateOrder(OrderCreateRequest request) {
        Website website = websiteService.getWebsiteById(request.getWebsiteId());

        Order order = orderMapper.toEntity(request, website);
        Order savedOrder = orderRepository.save(order);

        eventPublisher.publishEvent(new OrderCreatedEvent(savedOrder));

        return savedOrder;
    }

    @Transactional(readOnly = true)
    public Optional<Order> getOrderById(UUID orderId) {
        return orderRepository.findById(orderId);
    }
}
