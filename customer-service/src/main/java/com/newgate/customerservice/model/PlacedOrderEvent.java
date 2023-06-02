package com.newgate.customerservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;
import java.math.BigDecimal;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlacedOrderEvent {

    UUID id;
    UUID customerId;
    UUID productId;
    BigDecimal price;
    Integer quantity;

}
