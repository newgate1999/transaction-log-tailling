package com.newgate.orderservice.service;

import com.newgate.orderservice.model.OrderRequest;

import java.util.UUID;

public interface OrderService {

    void placeOrder(OrderRequest orderRequest);

    void updateOrderStatus(UUID orderId, boolean success);

    void batchInsert();
}
