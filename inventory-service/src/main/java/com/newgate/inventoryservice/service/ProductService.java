package com.newgate.inventoryservice.service;

import com.newgate.inventoryservice.model.PlacedOrderEvent;

public interface ProductService {

    boolean reserveProduct(PlacedOrderEvent orderEvent);
}
