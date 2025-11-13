package com.gaelcraves.project3.GaelCravings_Backend.Service;

import com.gaelcraves.project3.GaelCravings_Backend.Entity.FoodItem;
import com.gaelcraves.project3.GaelCravings_Backend.Repository.FoodItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class FoodItemService {

    private final FoodItemRepository repo;

    public FoodItemService(FoodItemRepository repo) {
        this.repo = repo;
    }

    @Transactional(readOnly = true)
    public List<FoodItem> findAll() {
        return repo.findAll();
    }

    @Transactional(readOnly = true)
    public FoodItem findById(Integer id) {
        return repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("FoodItem not found: " + id));
    }

    public FoodItem create(FoodItem item) {
        item.setFoodItemId(null);
        return repo.save(item);
    }

    public FoodItem update(Integer id, FoodItem incoming) {
        FoodItem existing = findById(id);
        existing.setCalories(incoming.getCalories());
        existing.setPrice(incoming.getPrice());
        return repo.save(existing);
    }

    public void delete(Integer id) {
        if (!repo.existsById(id)) {
            throw new IllegalArgumentException("FoodItem not found: " + id);
        }
        repo.deleteById(id);
    }
}
