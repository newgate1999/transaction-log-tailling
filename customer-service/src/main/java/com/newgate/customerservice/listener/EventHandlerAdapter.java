package com.newgate.customerservice.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.newgate.customerservice.domain.MessageLog;
import com.newgate.customerservice.domain.OutBox;
import com.newgate.customerservice.model.PlacedOrderEvent;
import com.newgate.customerservice.repository.MessageLogRepository;
import com.newgate.customerservice.repository.OutBoxRepository;
import com.newgate.customerservice.service.CustomerService;
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

@Slf4j
@Component
@RequiredArgsConstructor
public class EventHandlerAdapter {

    private final ObjectMapper mapper;

    private final MessageLogRepository messageLogRepository;

    private final OutBoxRepository outBoxRepository;

    private static final String CUSTOMER = "CUSTOMER";

    private static final String ORDER_CREATED = "ORDER_CREATED";

    private final CustomerService customerService;

    private static final String RESERVE_CUSTOMER_BALANCE_FAILED =
            "RESERVE_CUSTOMER_BALANCE_FAILED";

    private static final String RESERVE_CUSTOMER_BALANCE_SUCCESSFULLY =
            "RESERVE_CUSTOMER_BALANCE_SUCCESSFULLY";

    private static final String RESERVE_PRODUCT_STOCK_FAILED = "RESERVE_PRODUCT_STOCK_FAILED";

    @Bean
    @Transactional
    public Consumer<Message<String>> handleReserveCustomerBalanceRequest() {
        return event -> {
            var messageId = event.getHeaders().getId();
            if (Objects.nonNull(messageId) && !messageLogRepository.existsById(messageId)) {
                var eventType = getHeaderAsString(event.getHeaders(), "eventType");
                if (ORDER_CREATED.equals(eventType)) {
                    PlacedOrderEvent placedOrderEvent = deserialize(event.getPayload());
                    log.debug("Start process reserve customer balance {}", placedOrderEvent);
                    OutBox outbox = new OutBox();
                    outbox.setAggregateId(placedOrderEvent.getId());
                    outbox.setPayload(mapper.convertValue(placedOrderEvent, JsonNode.class));
                    outbox.setAggregateType(CUSTOMER);

                    if (customerService.reserveBalance(placedOrderEvent)) {
                        outbox.setType(RESERVE_CUSTOMER_BALANCE_SUCCESSFULLY);
                    } else {
                        outbox.setType(RESERVE_CUSTOMER_BALANCE_FAILED);
                    }

                    outBoxRepository.save(outbox);
                    log.debug("Done process reserve customer balance {}", placedOrderEvent);

                }
                messageLogRepository.save(new MessageLog(messageId, Timestamp.from(Instant.now())));
            }
        };
    }

    @Bean
    @Transactional
    public Consumer<Message<String>> handleCompensateCustomerBalanceRequest() {
        return event -> {
            var messageId = event.getHeaders().getId();
            if (Objects.nonNull(messageId) && !messageLogRepository.existsById(messageId)) {
                var eventType = getHeaderAsString(event.getHeaders(), "eventType");
                if (eventType.equals(RESERVE_PRODUCT_STOCK_FAILED)) {
                    var placedOrderEvent = deserialize(event.getPayload());

                    log.debug("Start process compensate customer balance {}", placedOrderEvent);
                    customerService.compensateBalance(placedOrderEvent);
                    log.debug("Done process compensate customer balance {}", placedOrderEvent);
                }
                // Marked message is processed
                messageLogRepository.save(new MessageLog(messageId, Timestamp.from(Instant.now())));
            }
        };
    }

    private String getHeaderAsString(MessageHeaders headers, String name) {
        var value = headers.get(name, byte[].class);
        if (Objects.isNull(value)) {
            throw new IllegalArgumentException(
                    String.format("Expected record header %s not present", name));
        }
        return new String(value, StandardCharsets.UTF_8);
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
}
