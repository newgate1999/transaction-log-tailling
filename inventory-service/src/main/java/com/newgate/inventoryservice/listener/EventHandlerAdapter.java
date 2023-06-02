package com.newgate.inventoryservice.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.newgate.inventoryservice.entity.MessageLog;
import com.newgate.inventoryservice.entity.OutBox;
import com.newgate.inventoryservice.model.PlacedOrderEvent;
import com.newgate.inventoryservice.repository.MessageLogRepository;
import com.newgate.inventoryservice.repository.OutBoxRepository;
import com.newgate.inventoryservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Objects;
import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventHandlerAdapter {

    private final ObjectMapper mapper;

    private final OutBoxRepository outBoxRepository;

    private final MessageLogRepository messageLogRepository;

    private final ProductService productService;

    private static final String PRODUCT = "PRODUCT";

    private static final String RESERVE_CUSTOMER_BALANCE_SUCCESSFULLY =
            "RESERVE_CUSTOMER_BALANCE_SUCCESSFULLY";

    private static final String RESERVE_PRODUCT_STOCK_FAILED = "RESERVE_PRODUCT_STOCK_FAILED";

    private static final String RESERVE_PRODUCT_STOCK_SUCCESSFULLY =
            "RESERVE_PRODUCT_STOCK_SUCCESSFULLY";

    @Bean
    @Transactional
    public Consumer<Message<String>> handleReserveProductStockRequest() {
        return event -> {
            var messageId = event.getHeaders().getId();
            if (Objects.nonNull(messageId) && !outBoxRepository.existsById(messageId)) {
                var placedOrderEvent = deserialize(event.getPayload());
                var eventType = getHeaderAsString(event.getHeaders(), "eventType");
                if (eventType.equals(RESERVE_CUSTOMER_BALANCE_SUCCESSFULLY)) {
                    OutBox outbox = OutBox.builder()
                            .aggregateId(placedOrderEvent.getId())
                            .payload(mapper.convertValue(placedOrderEvent, JsonNode.class))
                            .aggregateType(PRODUCT)
                            .build();
                    if (productService.reserveProduct(placedOrderEvent)) {
                        outbox.setType(RESERVE_PRODUCT_STOCK_SUCCESSFULLY);
                    } else {
                        outbox.setType(RESERVE_PRODUCT_STOCK_FAILED);
                    }

                    // Exported event into outbox table
                    outBoxRepository.save(outbox);
                    log.debug("Done process reserve product stock {}", placedOrderEvent);
                }

                // Marked message is processed
                messageLogRepository.save(new MessageLog(messageId, Timestamp.from(Instant.now())));

            }
        };
    }

    private PlacedOrderEvent deserialize(String event) {
        PlacedOrderEvent placedOrderEvent;
        try {
            String unescaped = mapper.readValue(event, String.class);
            placedOrderEvent = mapper.readValue(unescaped, PlacedOrderEvent.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Couldn't deserialize event", e);
        }
        return placedOrderEvent;
    }

    private String getHeaderAsString(MessageHeaders headers, String name) {
        var value = headers.get(name, byte[].class);
        if (Objects.isNull(value)) {
            throw new IllegalArgumentException(
                    String.format("Expected record header %s not present", name));
        }
        return new String(value, StandardCharsets.UTF_8);
    }
}
