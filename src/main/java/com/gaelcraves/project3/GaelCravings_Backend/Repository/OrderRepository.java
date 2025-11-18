package com.gaelcraves.project3.GaelCravings_Backend.Repository;

import com.gaelcraves.project3.GaelCravings_Backend.Entity.Order;
import com.gaelcraves.project3.GaelCravings_Backend.Entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByUser_UserId(Integer userId);
    List<Order> findByStatus(OrderStatus status);
    List<Order> findByUser_UserIdAndStatus(Integer userId, OrderStatus status);
}
