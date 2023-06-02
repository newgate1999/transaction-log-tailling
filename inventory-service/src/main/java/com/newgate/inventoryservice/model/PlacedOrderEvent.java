package com.newgate.inventoryservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

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
