package com.newgate.customerservice.service;

import com.newgate.customerservice.model.PlacedOrderEvent;

public interface CustomerService {

    boolean reserveBalance(PlacedOrderEvent orderEvent);

    void compensateBalance(PlacedOrderEvent orderEvent);
}
