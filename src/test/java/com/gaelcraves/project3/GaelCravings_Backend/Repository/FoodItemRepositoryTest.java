package com.gaelcraves.project3.GaelCravings_Backend.Repository;

import com.gaelcraves.project3.GaelCravings_Backend.Entity.FoodItem;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class FoodItemRepositoryTest {

    @Autowired
    private FoodItemRepository repo;

    private FoodItem makeItem(String name, int calories, BigDecimal price) {
        FoodItem item = new FoodItem();
        item.setFoodItemId(null);
        item.setName(name);
        item.setCalories(calories);
        item.setPrice(price);
        return item;
    }

    @Test
    @DisplayName("Saving a FoodItem assigns an ID")
    void save_assignsId() {
        FoodItem item = makeItem("Burger", 700, new BigDecimal("9.99"));

        FoodItem saved = repo.save(item);

        assertNotNull(saved.getFoodItemId());
    }

    @Test
    @DisplayName("findById returns the correct item")
    void findById_returnsItem() {
        FoodItem item = makeItem("Pizza", 900, new BigDecimal("12.50"));
        FoodItem saved = repo.save(item);

        FoodItem found = repo.findById(saved.getFoodItemId()).orElseThrow();

        assertEquals("Pizza", found.getName());
        assertEquals(900, found.getCalories());
    }

    @Test
    @DisplayName("Updating an item persists new values")
    void update_persistsChanges() {
        FoodItem item = makeItem("Fries", 300, new BigDecimal("3.00"));
        FoodItem saved = repo.save(item);

        saved.setCalories(350);
        saved.setPrice(new BigDecimal("3.50"));
        repo.save(saved);

        FoodItem updated = repo.findById(saved.getFoodItemId()).orElseThrow();

        assertEquals(350, updated.getCalories());
        assertEquals(new BigDecimal("3.50"), updated.getPrice());
    }

    @Test
    @DisplayName("Deleting an item removes it from the database")
    void delete_removesItem() {
        FoodItem item = makeItem("Salad", 200, new BigDecimal("7.25"));
        FoodItem saved = repo.save(item);

        repo.deleteById(saved.getFoodItemId());

        assertTrue(repo.findById(saved.getFoodItemId()).isEmpty());
    }

    @Test
    @DisplayName("findAll returns all saved items")
    void findAll_returnsAll() {
        repo.save(makeItem("Taco", 300, new BigDecimal("4.00")));
        repo.save(makeItem("Burrito", 800, new BigDecimal("8.50")));

        List<FoodItem> all = repo.findAll();

        assertEquals(2, all.size());
    }
}
