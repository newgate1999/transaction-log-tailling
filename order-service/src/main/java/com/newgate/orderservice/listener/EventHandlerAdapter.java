package com.newgate.orderservice.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.newgate.orderservice.entity.MessageLog;
import com.newgate.orderservice.entity.OutBox;
import com.newgate.orderservice.model.PlacedOrderEvent;
import com.newgate.orderservice.repository.MessageLogRepository;
import com.newgate.orderservice.repository.OutBoxRepository;
import com.newgate.orderservice.service.OrderService;
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

    private final OrderService orderService;

    private final MessageLogRepository messageLogRepository;

    public static final String ORDER = "ORDER";

    public static final String ORDER_CREATED = "ORDER_CREATED";

    private static final String RESERVE_CUSTOMER_BALANCE_SUCCESSFULLY =
            "RESERVE_CUSTOMER_BALANCE_SUCCESSFULLY";

    private static final String RESERVE_CUSTOMER_BALANCE_FAILED = "RESERVE_CUSTOMER_BALANCE_FAILED";

    private static final String RESERVE_PRODUCT_STOCK_SUCCESSFULLY =
            "RESERVE_PRODUCT_STOCK_SUCCESSFULLY";

    private static final String RESERVE_PRODUCT_STOCK_FAILED = "RESERVE_PRODUCT_STOCK_FAILED";

    private static final String COMPENSATE_CUSTOMER_BALANCE = "COMPENSATE_CUSTOMER_BALANCE";

    @Bean
    @Transactional
    public Consumer<Message<String>> reserveCustomerBalanceStage() {
        return event -> {
            var messageId = event.getHeaders().getId();
            if (Objects.nonNull(messageId) && !outBoxRepository.existsById(messageId)) {
                var placedOrderEvent = deserialize(event.getPayload());
                var eventType = getHeaderAsString(event.getHeaders(), "eventType");
                if (eventType.equals(RESERVE_CUSTOMER_BALANCE_SUCCESSFULLY)) {
                    OutBox outbox =
                            null;
                    outbox = OutBox.builder()
                            .aggregateId(placedOrderEvent.getId())
                            .payload(mapper.convertValue(placedOrderEvent, JsonNode.class))
                            .aggregateType(ORDER)
                            .type(RESERVE_CUSTOMER_BALANCE_SUCCESSFULLY)
                            .build();
                    outBoxRepository.save(outbox);
                } else if (eventType.equals(RESERVE_CUSTOMER_BALANCE_FAILED)) {
                    orderService.updateOrderStatus(placedOrderEvent.getId(), false);
                }

                // Marked message is processed
                messageLogRepository.save(new MessageLog(messageId, Timestamp.from(Instant.now())));

            }
        };
    }

    @Bean
    @Transactional
    public Consumer<Message<String>> reserveProductStockStage() {
        return event -> {
            var messageId = event.getHeaders().getId();
            if (Objects.nonNull(messageId) && !messageLogRepository.existsById(messageId)) {
                var placedOrderEvent = deserialize(event.getPayload());
                var eventType = getHeaderAsString(event.getHeaders(), "eventType");
                if (eventType.equals(RESERVE_PRODUCT_STOCK_SUCCESSFULLY)) {
                    orderService.updateOrderStatus(placedOrderEvent.getId(), true);
                } else if (eventType.equals(RESERVE_PRODUCT_STOCK_FAILED)) {
                    orderService.updateOrderStatus(placedOrderEvent.getId(), false);
                    var outbox =
                            OutBox.builder()
                                    .aggregateId(placedOrderEvent.getId())
                                    .aggregateType(ORDER)
                                    .type(COMPENSATE_CUSTOMER_BALANCE)
                                    .payload(mapper.convertValue(placedOrderEvent, JsonNode.class))
                                    .build();
                    outBoxRepository.save(outbox);
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
