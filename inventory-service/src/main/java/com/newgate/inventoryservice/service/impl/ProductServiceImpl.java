package com.newgate.inventoryservice.service.impl;

import com.newgate.inventoryservice.model.PlacedOrderEvent;
import com.newgate.inventoryservice.repository.ProductJpaRepository;
import com.newgate.inventoryservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductJpaRepository productJpaRepository;

    @Override
    public boolean reserveProduct(PlacedOrderEvent orderEvent) {
        var product = productJpaRepository.findById(orderEvent.getProductId());

        if (product.isEmpty()) {
            return false;
        }
        if (product.get().getStocks() - orderEvent.getQuantity() < 0) {
            return false;
        }
        product.get().setStocks(product.get().getStocks() - orderEvent.getQuantity());
        productJpaRepository.save(product.get());
        return true;
    }
}
