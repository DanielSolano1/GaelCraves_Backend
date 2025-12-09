package com.gaelcraves.project3.GaelCravings_Backend.Controller;

import com.gaelcraves.project3.GaelCravings_Backend.DTO.AdminStats;
import com.gaelcraves.project3.GaelCravings_Backend.Entity.Order;
import com.gaelcraves.project3.GaelCravings_Backend.Service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/orders/admin")
@CrossOrigin(origins = {
    "http://localhost:8081",
    "http://localhost:3000",
    "http://localhost:19006",
    "https://gaelcraves-frontend-7a6e5c03f69a.herokuapp.com"
})
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
    
    private final OrderService orderService;

    public AdminController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Get admin dashboard statistics
     * GET /api/orders/admin/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<AdminStats> getAdminStats() {
        try {
            logger.info("Fetching admin stats...");
            AdminStats stats = orderService.getAdminStats();
            
            if (stats == null) {
                logger.warn("OrderService.getAdminStats() returned null, creating default stats");
                stats = createDefaultStats();
            }
            
            logger.info("Admin stats fetched successfully: pendingOrders={}, todayRevenue={}, totalUsers={}, menuItems={}", 
                stats.getPendingOrders(), 
                stats.getTodayRevenue(), 
                stats.getTotalUsers(), 
                stats.getMenuItems());
            
            return ResponseEntity.ok(stats);
            
        } catch (Exception e) {
            logger.error("Error fetching admin stats", e);
            
            // Return default stats instead of error
            AdminStats defaultStats = createDefaultStats();
            return ResponseEntity.ok(defaultStats);
        }
    }

    /**
     * Get all orders in the system
     * GET /api/orders/admin/all
     */
    @GetMapping("/all")
    public ResponseEntity<List<Order>> getAllOrders() {
        try {
            logger.info("Fetching all orders...");
            List<Order> orders = orderService.getAllOrders();
            
            logger.info("Fetched {} orders", orders != null ? orders.size() : 0);
            
            return ResponseEntity.ok(orders != null ? orders : List.of());
            
        } catch (Exception e) {
            logger.error("Error fetching all orders", e);
            
            // Return empty list instead of error
            return ResponseEntity.ok(List.of());
        }
    }

    /**
     * Update order status (accept/decline)
     * PUT /api/orders/admin/{orderId}/status
     */
    @PutMapping("/{orderId}/status")
    public ResponseEntity<Order> updateOrderStatus(
            @PathVariable Integer orderId,
            @RequestParam String action) {
        try {
            logger.info("Updating order {} with action: {}", orderId, action);
            Order updatedOrder = orderService.updateOrderStatus(orderId, action);
            
            logger.info("Order {} status updated successfully to {}", orderId, updatedOrder.getStatus());
            
            return ResponseEntity.ok(updatedOrder);
            
        } catch (IllegalArgumentException e) {
            logger.error("Invalid order ID or action: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            
        } catch (Exception e) {
            logger.error("Error updating order status for orderId: " + orderId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Helper method to create default stats when there's an error
     */
    private AdminStats createDefaultStats() {
        AdminStats stats = new AdminStats();
        stats.setPendingOrders(0);
        stats.setTodayRevenue(BigDecimal.ZERO);
        stats.setTotalUsers(0);
        stats.setMenuItems(0);
        return stats;
    }
}
