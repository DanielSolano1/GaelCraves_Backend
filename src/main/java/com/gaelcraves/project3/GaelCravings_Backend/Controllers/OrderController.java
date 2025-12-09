package com.gaelcraves.project3.GaelCravings_Backend.Controllers;

import com.gaelcraves.project3.GaelCravings_Backend.DTO.AdminStats;
import com.gaelcraves.project3.GaelCravings_Backend.DTO.OrderItemRequest;
import com.gaelcraves.project3.GaelCravings_Backend.DTO.OrderStatus;
import com.gaelcraves.project3.GaelCravings_Backend.Entity.Order;
import com.gaelcraves.project3.GaelCravings_Backend.Service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * ADMIN: Get all orders (with optional status filter)
     */
    @GetMapping("/admin/all")
    public ResponseEntity<List<Order>> getAllOrders(@RequestParam(value = "status", required = false) String status) {
        if (status != null) {
            OrderStatus os = OrderStatus.valueOf(status);
            return ResponseEntity.ok(orderService.getOrdersByStatus(os));
        }
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    /**
     * ADMIN: Dashboard statistics
     */
    @GetMapping("/admin/stats")
    public ResponseEntity<AdminStats> getAdminStats() {
        AdminStats stats = orderService.getAdminStats();
        return ResponseEntity.ok(stats);
    }

    /**
     * Create Stripe PaymentIntent and return client secret
     */
    @PostMapping("/create-payment-intent")
    public ResponseEntity<?> createPaymentIntent(@RequestBody Map<String, Object> request) {
        try {
            String mealPrice = (String) request.get("mealPrice");
            long amount = (long) (Double.parseDouble(mealPrice.replace("$", "")) * 100);

            String stripeSecretKey = System.getenv("STRIPE_SECRET_KEY");
            if (stripeSecretKey == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("error", "Stripe secret key not configured"));
            }
            Stripe.apiKey = stripeSecretKey;

            Map<String, Object> params = new HashMap<>();
            params.put("amount", amount);
            params.put("currency", "usd");
            params.put("automatic_payment_methods", Map.of("enabled", true));

            PaymentIntent intent = PaymentIntent.create(params);
            return ResponseEntity.ok(Map.of("clientSecret", intent.getClientSecret()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to create PaymentIntent: " + e.getMessage()));
        }
    }

    /**
     * Process payment for order (mock implementation)
     */
    @PostMapping("/payment")
    @SuppressWarnings("unused")
    public ResponseEntity<?> processPayment(@RequestBody Map<String, Object> request) {
        try {
            String mealTitle = (String) request.get("mealTitle");
            String mealPrice = (String) request.get("mealPrice");
            String orderTime = (String) request.get("orderTime");
            String specialNotes = (String) request.get("specialNotes");

            Map<String, Object> paymentResult = Map.of(
                    "success", true,
                    "orderId", "GC" + System.currentTimeMillis() % 100000,
                    "status", "CONFIRMED",
                    "amount", mealPrice,
                    "mealTitle", mealTitle,
                    "orderTime", orderTime != null ? orderTime : "ASAP",
                    "specialNotes", specialNotes != null ? specialNotes : "None",
                    "message", "Payment processed successfully"
            );

            return ResponseEntity.ok(paymentResult);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Payment processing failed: " + e.getMessage()));
        }
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
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
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
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
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
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
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
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}