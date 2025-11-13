package com.gaelcraves.project3.GaelCravings_Backend;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/food-items")
public class FoodItemController {

    private final FoodItemService service;

    public FoodItemController(FoodItemService service) {
        this.service = service;
    }

    @GetMapping
    public List<FoodItem> list() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public FoodItem get(@PathVariable Integer id) {
        return service.findById(id);
    }

    @PostMapping
    public ResponseEntity<FoodItem> create(@Valid @RequestBody FoodItem item) {
        FoodItem saved = service.create(item);
        return ResponseEntity.created(URI.create("/api/food-items/" + saved.getFoodItemId()))
                .body(saved);
    }

    @PutMapping("/{id}")
    public FoodItem update(@PathVariable Integer id, @Valid @RequestBody FoodItem item) {
        return service.update(id, item);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Optional: convert IllegalArgumentException to 404
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleNotFound(IllegalArgumentException ex) {
        return ResponseEntity.notFound().build();
    }
}
