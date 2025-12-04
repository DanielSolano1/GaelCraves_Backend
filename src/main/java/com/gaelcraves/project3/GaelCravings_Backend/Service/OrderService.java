package com.gaelcraves.project3.GaelCravings_Backend.Service;

import com.gaelcraves.project3.GaelCravings_Backend.DTO.AdminStats;
import com.gaelcraves.project3.GaelCravings_Backend.DTO.OrderItemRequest;
import com.gaelcraves.project3.GaelCravings_Backend.DTO.OrderStatus;
import com.gaelcraves.project3.GaelCravings_Backend.Entity.*;
import com.gaelcraves.project3.GaelCravings_Backend.Repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final FoodItemRepository foodItemRepository;

    public OrderService(OrderRepository orderRepository,
                        UserRepository userRepository,
                        FoodItemRepository foodItemRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.foodItemRepository = foodItemRepository;
    }

    /**
     * Create a new order
     */
    public Order createOrder(Integer userId, List<OrderItemRequest> items) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Order order = new Order();
        order.setUser(user);

        // Add items to order
        for (OrderItemRequest itemRequest : items) {
            FoodItem foodItem = foodItemRepository.findById(itemRequest.getFoodItemId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Food item not found: " + itemRequest.getFoodItemId()
                    ));

            OrderItem orderItem = new OrderItem();
            orderItem.setFoodItem(foodItem);
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setPrice(foodItem.getPrice()); // Snapshot current price
            orderItem.setSpecialInstructions(itemRequest.getSpecialInstructions());

            order.addOrderItem(orderItem); // Use helper method
        }

        return orderRepository.save(order);
    }

    /**
     * Update order status
     */
    public Order confirmOrder(Integer orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        order.confirm(); // Use helper method
        return orderRepository.save(order);
    }

    /**
     * Cancel order
     */
    public Order cancelOrder(Integer orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        order.cancel(); // Use helper method
        return orderRepository.save(order);
    }

    /**
     * Update item quantity in order
     */
    public Order updateItemQuantity(Integer orderId, Integer foodItemId, Integer newQuantity) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        order.updateItemQuantity(foodItemId, newQuantity); // Use helper method
        return orderRepository.save(order);
    }

    /**
     * Get all orders for a user
     */
    public List<Order> getUserOrders(Integer userId) {
        return orderRepository.findByUser_UserId(userId);
    }

    /**
     * Get order total
     */
    public String getOrderTotal(Integer orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        return "Total: $" + order.getTotalAmount() +
                " (" + order.getTotalItemCount() + " items)";
    }

    public Order getOrderById(Integer orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
    }

    public Order updateOrderStatus(Integer orderId, String action) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        OrderStatus os = OrderStatus.valueOf(action);
        order.setStatus(os);

        return order;
    }

    /**
     * ADMIN: Get all orders in the system
     */
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    /**
     * ADMIN: Get all orders with a specific status
     */
    public List<Order> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    /**
     * ADMIN: Simple aggregate statistics for dashboard
     */
    public AdminStats getAdminStats() {
        List<Order> allOrders = orderRepository.findAll();

        long pendingOrders = allOrders.stream()
                .filter(o -> o.getStatus() == OrderStatus.PENDING)
                .count();

    LocalDate today = LocalDate.now();
    LocalDateTime startOfDay = today.atStartOfDay();
    LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();
        BigDecimal todayRevenue = allOrders.stream()
        .filter(o -> o.getStatus() == OrderStatus.CONFIRMED)
        .filter(o -> o.getOrderDate() != null &&
            !o.getOrderDate().isBefore(startOfDay) &&
            o.getOrderDate().isBefore(endOfDay))
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long totalUsers = userRepository.count();
        long menuItems = foodItemRepository.count();

        AdminStats stats = new AdminStats();
        stats.setPendingOrders((int) pendingOrders);
        stats.setTodayRevenue(todayRevenue);
        stats.setTotalUsers((int) totalUsers);
        stats.setMenuItems((int) menuItems);
        return stats;
    }
}
