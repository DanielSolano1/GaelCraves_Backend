package com.gaelcraves.project3.GaelCravings_Backend.Repository;

import com.gaelcraves.project3.GaelCravings_Backend.Entity.FoodItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FoodItemRepository extends JpaRepository<FoodItem, Integer> {

}
