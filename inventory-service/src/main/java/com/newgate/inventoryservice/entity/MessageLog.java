package com.newgate.inventoryservice.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.sql.Timestamp;
import java.util.UUID;

@Data
@Entity(name = "message_log")
@NoArgsConstructor
@AllArgsConstructor
public class MessageLog {

    @Id
    private UUID id;

    @Column(nullable = false)
    private Timestamp receivedAt;
}
