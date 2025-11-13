package com.gaelcraves.project3.GaelCravings_Backend.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Entity
@Table(name = "food_item")
public class FoodItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer foodItemId;

    @NotNull
    @Column(nullable = false)
    private Integer calories;

    @NotNull
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "menu_id")
    private Menu menu;


    public FoodItem() {}

    public Integer getFoodItemId() { return foodItemId; }
    public void setFoodItemId(Integer foodItemId) { this.foodItemId = foodItemId; }

    public Integer getCalories() { return calories; }
    public void setCalories(Integer calories) { this.calories = calories; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public Menu getMenu() { return menu; }
    public void setMenu(Menu menu) { this.menu = menu; }
}
