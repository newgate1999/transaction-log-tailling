package com.newgate.inventoryservice.repository;

import com.newgate.inventoryservice.entity.MessageLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MessageLogRepository extends JpaRepository<MessageLog, UUID> {
}
