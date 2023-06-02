package com.newgate.orderservice.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.newgate.orderservice.entity.OrderEntity;
import com.newgate.orderservice.entity.OrderStatus;
import com.newgate.orderservice.entity.OutBox;
import com.newgate.orderservice.model.OrderRequest;
import com.newgate.orderservice.repository.OrderJpaRepository;
import com.newgate.orderservice.repository.OutBoxRepository;
import com.newgate.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final ObjectMapper mapper;

    private final OrderJpaRepository orderJpaRepository;

    private final OutBoxRepository outBoxRepository;

    public static final String ORDER_CREATED = "ORDER_CREATED";

    public static final String ORDER = "ORDER";

    @Override
    public void placeOrder(OrderRequest orderRequest) {
        var order = mapper.convertValue(orderRequest, OrderEntity.class);
        order.setCreatedAt(Timestamp.from(Instant.now()));
        order.setId(UUID.randomUUID());
        order.setStatus(OrderStatus.PENDING);
        orderJpaRepository.save(order);
        exportOutBoxEvent(order);
    }

    @Override
    public void updateOrderStatus(UUID orderId, boolean success) {

        var order = orderJpaRepository.findById(orderId);
        if (order.isPresent()) {
            if (success) {
                order.get().setStatus(OrderStatus.COMPLETED);
            } else {
                order.get().setStatus(OrderStatus.CANCELED);
            }
            orderJpaRepository.save(order.get());
        }

    }

    @Override
    public void batchInsert() {

    }

    private void exportOutBoxEvent(OrderEntity order) {
        var outbox =
                OutBox.builder()
                        .aggregateId(order.getId())
                        .aggregateType(ORDER)
                        .type(ORDER_CREATED)
                        .payload(mapper.convertValue(order, JsonNode.class))
                        .build();
        outBoxRepository.save(outbox);
    }
}
