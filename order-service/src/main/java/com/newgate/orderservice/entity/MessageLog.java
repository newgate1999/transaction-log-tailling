package com.newgate.orderservice.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import jakarta.persistence.*;
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
