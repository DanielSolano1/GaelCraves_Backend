package com.gaelcraves.project3.GaelCravings_Backend.Controllers;

import com.gaelcraves.project3.GaelCravings_Backend.DTO.OrderItemRequest;
import com.gaelcraves.project3.GaelCravings_Backend.Entity.Order;
import com.gaelcraves.project3.GaelCravings_Backend.Service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Create a new order
     */
    @PostMapping
    public ResponseEntity<?> createOrder(
            @RequestBody Map<String, Object> request,
            Authentication auth) {
        try {
            Integer userId = (Integer) request.get("userId");
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> items = (List<Map<String, Object>>) request.get("items");

            // Convert to OrderItemRequest list
            List<OrderItemRequest> orderItems = items.stream()
                    .map(item -> {
                        OrderItemRequest req = new OrderItemRequest();
                        req.setFoodItemId((Integer) item.get("foodItemId"));
                        req.setQuantity((Integer) item.get("quantity"));
                        req.setSpecialInstructions((String) item.get("specialInstructions"));
                        return req;
                    })
                    .toList();

            Order order = orderService.createOrder(userId, orderItems);
            return ResponseEntity.status(HttpStatus.CREATED).body(order);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get all orders for a user
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserOrders(@PathVariable Integer userId) {
        List<Order> orders = orderService.getUserOrders(userId);
        return ResponseEntity.ok(orders);
    }

    /**
     * Get order by ID
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrder(@PathVariable Integer orderId) {
        try {
            Order order = orderService.getOrderById(orderId);
            return ResponseEntity.ok(order);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Update order status
     */
    @PutMapping("/{orderId}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable Integer orderId,
            @RequestBody Map<String, String> body) {
        try {
            String action = body.get("action");
            Order updated = orderService.updateOrderStatus(orderId, action);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Cancel order
     */
    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<?> cancelOrder(@PathVariable Integer orderId) {
        try {
            Order cancelled = orderService.cancelOrder(orderId);
            return ResponseEntity.ok(cancelled);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Update item quantity
     */
    @PutMapping("/{orderId}/items/{foodItemId}")
    public ResponseEntity<?> updateItemQuantity(
            @PathVariable Integer orderId,
            @PathVariable Integer foodItemId,
            @RequestBody Map<String, Integer> body) {
        try {
            Integer newQuantity = body.get("quantity");
            Order updated = orderService.updateItemQuantity(orderId, foodItemId, newQuantity);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
}