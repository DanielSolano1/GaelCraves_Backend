package com.gaelcraves.project3.GaelCravings_Backend.Service;

import com.gaelcraves.project3.GaelCravings_Backend.Entity.Address;
import com.gaelcraves.project3.GaelCravings_Backend.Repository.AddressRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AddressService {

    private final AddressRepository repo;

    public AddressService(AddressRepository repo) {
        this.repo = repo;
    }

    @Transactional(readOnly = true)
    public List<Address> findAll() {
        return repo.findAll();
    }

    @Transactional(readOnly = true)
    public Address findById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Address not found: " + id));
    }

    public Address create(Address address) {
        return repo.save(address);
    }

    public Address update(Long id, Address incoming) {
        Address existing = findById(id);
        existing.setStreet(incoming.getStreet());
        existing.setCity(incoming.getCity());
        existing.setState(incoming.getState());
        existing.setPostalCode(incoming.getPostalCode());
        existing.setCountry(incoming.getCountry());
        return repo.save(existing);
    }

    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new IllegalArgumentException("Address not found: " + id);
        }
        repo.deleteById(id);
    }
}
