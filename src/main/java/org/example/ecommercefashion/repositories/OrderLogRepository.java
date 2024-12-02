package org.example.ecommercefashion.repositories;

import org.example.ecommercefashion.entities.OrderLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderLogRepository extends JpaRepository<OrderLog , Long> {}
