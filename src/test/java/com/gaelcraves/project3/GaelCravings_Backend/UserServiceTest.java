package com.gaelcraves.project3.GaelCravings_Backend;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.gaelcraves.project3.GaelCravings_Backend.DTO.OrderItemRequest;
import com.gaelcraves.project3.GaelCravings_Backend.DTO.OrderStatus;
import com.gaelcraves.project3.GaelCravings_Backend.Entity.FoodItem;
import com.gaelcraves.project3.GaelCravings_Backend.Entity.Order;
import com.gaelcraves.project3.GaelCravings_Backend.Entity.OrderItem;
import com.gaelcraves.project3.GaelCravings_Backend.Entity.User;
import com.gaelcraves.project3.GaelCravings_Backend.Repository.FoodItemRepository;
import com.gaelcraves.project3.GaelCravings_Backend.Repository.OrderRepository;
import com.gaelcraves.project3.GaelCravings_Backend.Repository.UserRepository;
import com.gaelcraves.project3.GaelCravings_Backend.Service.OrderService;

class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FoodItemRepository foodItemRepository;

    @InjectMocks
    private OrderService orderService;

    private User testUser;
    private FoodItem testFoodItem;
    private Order testOrder;
    private OrderItem testOrderItem;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup test user
        testUser = new User();
        testUser.setUserId(1);
        testUser.setEmail("test@example.com");

        // Setup test food item
        testFoodItem = new FoodItem();
        testFoodItem.setFoodItemId(1);
        testFoodItem.setPrice(new BigDecimal("12.99"));
        testFoodItem.setCalories(500);

        // Setup test order item
        testOrderItem = new OrderItem();
        testOrderItem.setOrderItemId(1);
        testOrderItem.setFoodItem(testFoodItem);
        testOrderItem.setQuantity(2);
        testOrderItem.setPrice(new BigDecimal("12.99"));

        // Setup test order
        testOrder = new Order();
        testOrder.setOrderId(1);
        testOrder.setUser(testUser);
        testOrder.setOrderDate(LocalDateTime.now());
        testOrder.setStatus(OrderStatus.PENDING);
        testOrder.setTotalAmount(new BigDecimal("25.98"));
        testOrder.setOrderItems(new ArrayList<>(List.of(testOrderItem)));
        testOrderItem.setOrder(testOrder);
    }

    // ============= CREATE ORDER TESTS =============

    @Test
    @DisplayName("Should create order successfully")
    void testCreateOrderSuccess() {
        // Arrange
        OrderItemRequest itemRequest = new OrderItemRequest();
        itemRequest.setFoodItemId(1);
        itemRequest.setQuantity(2);
        itemRequest.setSpecialInstructions("No onions");

        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(foodItemRepository.findById(1)).thenReturn(Optional.of(testFoodItem));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // Act
        Order result = orderService.createOrder(1, List.of(itemRequest));

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getOrderId());
        assertEquals(OrderStatus.PENDING, result.getStatus());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("Should throw exception when user not found during order creation")
    void testCreateOrderUserNotFound() {
        // Arrange
        OrderItemRequest itemRequest = new OrderItemRequest();
        itemRequest.setFoodItemId(1);
        itemRequest.setQuantity(2);

        when(userRepository.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> orderService.createOrder(1, List.of(itemRequest))
        );

        assertEquals("User not found", exception.getMessage());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Should throw exception when food item not found during order creation")
    void testCreateOrderFoodItemNotFound() {
        // Arrange
        OrderItemRequest itemRequest = new OrderItemRequest();
        itemRequest.setFoodItemId(999);
        itemRequest.setQuantity(2);

        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(foodItemRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> orderService.createOrder(1, List.of(itemRequest))
        );

        assertTrue(exception.getMessage().contains("Food item not found"));
        verify(orderRepository, never()).save(any(Order.class));
    }

    // ============= CONFIRM ORDER TESTS =============

    @Test
    @DisplayName("Should confirm order successfully")
    void testConfirmOrderSuccess() {
        // Arrange
        when(orderRepository.findById(1)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // Act
        Order result = orderService.confirmOrder(1);

        // Assert
        assertNotNull(result);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("Should throw exception when confirming non-existent order")
    void testConfirmOrderNotFound() {
        // Arrange
        when(orderRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> orderService.confirmOrder(999)
        );

        assertEquals("Order not found", exception.getMessage());
    }

    // ============= CANCEL ORDER TESTS =============

    @Test
    @DisplayName("Should cancel order successfully")
    void testCancelOrderSuccess() {
        // Arrange
        when(orderRepository.findById(1)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // Act
        Order result = orderService.cancelOrder(1);

        // Assert
        assertNotNull(result);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("Should throw exception when canceling non-existent order")
    void testCancelOrderNotFound() {
        // Arrange
        when(orderRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> orderService.cancelOrder(999)
        );

        assertEquals("Order not found", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when canceling non-cancellable order")
    void testCancelOrderNotCancellable() {
        // Arrange
        testOrder.setStatus(OrderStatus.DELIVERED);
        when(orderRepository.findById(1)).thenReturn(Optional.of(testOrder));

        // Act & Assert
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> orderService.cancelOrder(1)
        );

        assertTrue(exception.getMessage().contains("cannot be cancelled"));
    }

    // ============= UPDATE ITEM QUANTITY TESTS =============

    @Test
    @DisplayName("Should update item quantity successfully")
    void testUpdateItemQuantitySuccess() {
        // Arrange
        when(orderRepository.findById(1)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // Act
        Order result = orderService.updateItemQuantity(1, 1, 3);

        // Assert
        assertNotNull(result);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("Should throw exception when updating quantity for non-existent order")
    void testUpdateItemQuantityOrderNotFound() {
        // Arrange
        when(orderRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> orderService.updateItemQuantity(999, 1, 3)
        );

        assertEquals("Order not found", exception.getMessage());
    }

    // ============= GET USER ORDERS TESTS =============

    @Test
    @DisplayName("Should return all orders for a user")
    void testGetUserOrders() {
        // Arrange
        Order order2 = new Order();
        order2.setOrderId(2);
        order2.setUser(testUser);
        order2.setStatus(OrderStatus.CONFIRMED);

        List<Order> orders = Arrays.asList(testOrder, order2);
        when(orderRepository.findByUser_UserId(1)).thenReturn(orders);

        // Act
        List<Order> result = orderService.getUserOrders(1);

        // Assert
        assertEquals(2, result.size());
        verify(orderRepository, times(1)).findByUser_UserId(1);
    }

    @Test
    @DisplayName("Should return empty list when user has no orders")
    void testGetUserOrdersEmpty() {
        // Arrange
        when(orderRepository.findByUser_UserId(1)).thenReturn(new ArrayList<>());

        // Act
        List<Order> result = orderService.getUserOrders(1);

        // Assert
        assertTrue(result.isEmpty());
        verify(orderRepository, times(1)).findByUser_UserId(1);
    }

    // ============= GET ORDER BY ID TESTS =============

    @Test
    @DisplayName("Should return order by ID")
    void testGetOrderById() {
        // Arrange
        when(orderRepository.findById(1)).thenReturn(Optional.of(testOrder));

        // Act
        Order result = orderService.getOrderById(1);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getOrderId());
        verify(orderRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("Should throw exception when order not found by ID")
    void testGetOrderByIdNotFound() {
        // Arrange
        when(orderRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> orderService.getOrderById(999)
        );

        assertEquals("Order not found", exception.getMessage());
    }

    // ============= GET ORDER TOTAL TESTS =============

    @Test
    @DisplayName("Should calculate order total correctly")
    void testGetOrderTotal() {
        // Arrange
        when(orderRepository.findById(1)).thenReturn(Optional.of(testOrder));

        // Act
        String result = orderService.getOrderTotal(1);

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("$25.98"));
        assertTrue(result.contains("2 items"));
        verify(orderRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("Should throw exception when getting total for non-existent order")
    void testGetOrderTotalNotFound() {
        // Arrange
        when(orderRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> orderService.getOrderTotal(999)
        );

        assertEquals("Order not found", exception.getMessage());
    }

    // ============= UPDATE ORDER STATUS TESTS =============

    @Test
    @DisplayName("Should update order status successfully")
    void testUpdateOrderStatus() {
        // Arrange
        when(orderRepository.findById(1)).thenReturn(Optional.of(testOrder));

        // Act
        Order result = orderService.updateOrderStatus(1, "CONFIRMED");

        // Assert
        assertNotNull(result);
        assertEquals(OrderStatus.CONFIRMED, result.getStatus());
    }

    @Test
    @DisplayName("Should throw exception when updating status for non-existent order")
    void testUpdateOrderStatusNotFound() {
        // Arrange
        when(orderRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> orderService.updateOrderStatus(999, "CONFIRMED")
        );

        assertEquals("Order not found", exception.getMessage());
    }
}