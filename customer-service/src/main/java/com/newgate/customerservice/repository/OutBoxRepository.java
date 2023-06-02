package com.newgate.customerservice.repository;


import com.newgate.customerservice.domain.OutBox;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OutBoxRepository extends JpaRepository<OutBox, UUID> {
    boolean existsById(UUID uuid);
}
