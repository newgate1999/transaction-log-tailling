package com.newgate.customerservice.service;

import com.newgate.customerservice.model.PlacedOrderEvent;
import com.newgate.customerservice.repository.CustomerJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CustomerServiceImpl implements CustomerService {

    private final CustomerJpaRepository customerJpaRepository;

    @Override
    public boolean reserveBalance(PlacedOrderEvent orderEvent) {
        var customer = customerJpaRepository.findById(orderEvent.getCustomerId());
        if (customer.get()
                .getBalance()
                .subtract(orderEvent.getPrice()
                        .multiply(BigDecimal.valueOf(orderEvent.getQuantity())))
                .compareTo(BigDecimal.ZERO) < 0) {
            return false;
        }
        customer.get().setBalance(customer
                .get().getBalance()
                .subtract(orderEvent
                        .getPrice()
                        .multiply(BigDecimal.valueOf(orderEvent.getQuantity()))));

        return true;
    }

    @Override
    public void compensateBalance(PlacedOrderEvent orderEvent) {
        log.info("revert order: {}", orderEvent.getId());
        var customer = customerJpaRepository.findById(orderEvent.getCustomerId());
        if (customer.isEmpty()) {
            return;
        }
        customer.get().setBalance(
                customer.get().getBalance()
                        .add(orderEvent.getPrice().multiply(BigDecimal.valueOf(orderEvent.getQuantity()))));
        customerJpaRepository.save(customer.get());
    }
}
