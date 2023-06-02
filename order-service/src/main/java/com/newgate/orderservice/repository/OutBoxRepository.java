package com.newgate.orderservice.repository;

import com.newgate.orderservice.entity.OutBox;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OutBoxRepository extends JpaRepository<OutBox, UUID> {

    boolean existsById(UUID uuid);
}
