package com.newgate.orderservice.repository;

import com.newgate.orderservice.entity.MessageLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MessageLogRepository extends JpaRepository<MessageLog, UUID> {
}
