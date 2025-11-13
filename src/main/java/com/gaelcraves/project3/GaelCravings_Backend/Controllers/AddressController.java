package com.gaelcraves.project3.GaelCravings_Backend.Controllers;

import com.gaelcraves.project3.GaelCravings_Backend.Entity.Address;
import com.gaelcraves.project3.GaelCravings_Backend.Service.AddressService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/addresses")
public class AddressController {

    private final AddressService service;

    public AddressController(AddressService service) {
        this.service = service;
    }

    @GetMapping
    public List<Address> list() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Address get(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    public ResponseEntity<Address> create(@Valid @RequestBody Address address) {
        Address saved = service.create(address);
        return ResponseEntity
                .created(URI.create("/api/addresses/" + saved.getId()))
                .body(saved);
    }

    @PutMapping("/{id}")
    public Address update(@PathVariable Long id, @Valid @RequestBody Address address) {
        return service.update(id, address);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Map not-found to 404 instead of 500
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Void> handleNotFound() {
        return ResponseEntity.notFound().build();
    }
}
