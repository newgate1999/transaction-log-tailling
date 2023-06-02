package com.newgate.customerservice.repository;

import com.newgate.customerservice.domain.MessageLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MessageLogRepository extends JpaRepository<MessageLog, UUID> {
}
