package com.gaelcraves.project3.GaelCravings_Backend.Controllers;

import com.gaelcraves.project3.GaelCravings_Backend.Entity.Menu;
import com.gaelcraves.project3.GaelCravings_Backend.Service.MenuService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/menus")
public class MenuController {

    private final MenuService service;

    public MenuController(MenuService service) {
        this.service = service;
    }

    @GetMapping
    public List<Menu> list() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Menu get(@PathVariable Integer id) {
        return service.findById(id);
    }

    @PostMapping
    public ResponseEntity<Menu> create(@Valid @RequestBody Menu menu) {
        Menu saved = service.create(menu);
        return ResponseEntity.created(URI.create("/api/menus/" + saved.getMenuId()))
                .body(saved);
    }

    @PutMapping("/{id}")
    public Menu update(@PathVariable Integer id, @Valid @RequestBody Menu menu) {
        return service.update(id, menu);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Void> handleNotFound() {
        return ResponseEntity.notFound().build();
    }
}
