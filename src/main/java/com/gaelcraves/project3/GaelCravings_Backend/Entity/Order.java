package com.gaelcraves.project3.GaelCravings_Backend.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cascade;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Table(
        name = "orders"
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    @Column(nullable = false)
    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false)
    private BigDecimal totalAmount;

    // Helper
    public void removeOrderItem(OrderItem item) {
        orderItems.remove(item);
        item.setOrder(null);
    }
    public void addOrderItem(OrderItem item) {
        orderItems.add(item);
        item.setOrder(this);
    }

    public BigDecimal calculateTotal() {
        return orderItems.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public int getTotalItemCount() {
        return orderItems.stream()
                .mapToInt(OrderItem::getQuantity)
                .sum();
    }

    public boolean canBeCancelled() {
        return (status == OrderStatus.PENDING || status == OrderStatus.CONFIRMED);
    }

    public boolean canBeModified() {
        return (status == OrderStatus.PENDING);
    }

    public void cancel() {
        if (!canBeCancelled()) {
            throw new IllegalStateException(
                    "Order cannot be cancelled in " + status + " status"
            );
        }
        this.status = OrderStatus.CANCELLED;
    }

    public void confirm() {
        if (status != OrderStatus.PENDING) {
            throw new IllegalStateException(
                    "Order cannot be confirmed in " + status + " status"
            );
        }
        this.status = OrderStatus.CONFIRMED;
    }

    public void prepare() {
        if(status != OrderStatus.CONFIRMED) {
            throw new IllegalStateException(
                    "Order cannot be prepared in " + status  + " status"
            );
        }
        this.status = OrderStatus.PREPARING;
    }

    public void startDelivery() {
        if (status != OrderStatus.PREPARING) {
            throw new IllegalStateException(
                    "Delivery cannot start in " + status  + " status"
            );
        }
        this.status = OrderStatus.DELIVERING;
    }

    public void markAsDelivered() {
        if (status != OrderStatus.DELIVERING){
            throw new IllegalStateException(
                    "Order cannot be delivered in " + status  + " status"
            );
        }
        this.status = OrderStatus.DELIVERED;
    }

    public void pickupReady() {
        if (status != OrderStatus.PREPARING) {
            throw new IllegalStateException(
                    "Order cannot be picked up in " + status  + " status"
            );
        }
        this.status = OrderStatus.READY_FOR_PICKUP;
    }

    public void markAsPickedUp() {
        if (status != OrderStatus.READY_FOR_PICKUP) {
            throw new IllegalStateException(
                    "Order cannot be marked as picked up in " + status  + " status"
            );
        }
        this.status = OrderStatus.PICKED_UP;
    }

    @PrePersist
    protected void onCreate() {
        if (orderDate == null) {
            orderDate = LocalDateTime.now();
        }
        if (status == null) {
            status = OrderStatus.PENDING;
        }
        totalAmount = calculateTotal();
    }

    @PreUpdate
    protected void onUpdate() {
        totalAmount = calculateTotal();
    }

    public boolean isEmpty() {
        return orderItems == null || orderItems.isEmpty();
    }

    public boolean isActive() {
        return status != OrderStatus.CANCELLED &&
                status != OrderStatus.DELIVERED &&
                status != OrderStatus.PICKED_UP &&
                status != OrderStatus.REFUNDED;
    }

    public boolean isCompleted() {
        return status == OrderStatus.DELIVERED ||
                status == OrderStatus.PICKED_UP;
    }

    public void clearItems() {
        if (!canBeModified()) {
            throw new IllegalStateException(
                    "Cannot modify order in " + status + " status"
            );
        }
        orderItems.clear();
    }

    public void updateItemQuantity(Integer foodItemId, Integer newQuantity) {
        if (!canBeModified()) {
            throw new IllegalStateException(
                    "Cannot modify order in " + status + " status"
            );
        }

        OrderItem item = orderItems.stream()
                .filter(oi -> oi.getFoodItem().getFoodItemId().equals(foodItemId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Item not found in order"
                ));

        if (newQuantity <= 0) {
            removeOrderItem(item);
        } else {
            item.setQuantity(newQuantity);
        }
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", userId=" + (user != null ? user.getUserId() : null) +
                ", itemCount=" + getTotalItemCount() +
                ", totalAmount=" + totalAmount +
                ", status=" + status +
                ", orderDate=" + orderDate +
                '}';
    }
}
