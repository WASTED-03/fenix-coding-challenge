package com.commerce.ingestion_service.controller;

import com.commerce.ingestion.api.OrdersApi;
import com.commerce.ingestion.dto.Order;
import com.commerce.ingestion.dto.OrderCreateRequest;
import com.commerce.ingestion_service.mapper.OrderMapper;
import com.commerce.ingestion_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class OrderController implements OrdersApi {

    private final OrderService orderService;
    private final OrderMapper orderMapper;

    @Override
    public ResponseEntity<Order> createOrder(OrderCreateRequest orderCreateRequest) {
        com.commerce.ingestion_service.domain.Order createdOrder = orderService.createOrUpdateOrder(orderCreateRequest);
        
        return new ResponseEntity<>(orderMapper.toDto(createdOrder), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Order> getOrderById(UUID orderId) {
        return orderService.getOrderById(orderId)
                .map(order -> new ResponseEntity<>(orderMapper.toDto(order), HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
