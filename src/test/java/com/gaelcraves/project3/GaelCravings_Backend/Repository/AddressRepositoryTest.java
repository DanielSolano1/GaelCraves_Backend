package com.gaelcraves.project3.GaelCravings_Backend.Repository;

import com.gaelcraves.project3.GaelCravings_Backend.Entity.Address;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class AddressRepositoryTest {

    @Autowired
    private AddressRepository addressRepository;

    private Address createAddress() {
        Address a = new Address();
        // adjust these setters to match your actual Address fields
        a.setStreet("123 Main St");
        a.setCity("Santa Cruz");
        a.setState("CA");
        a.setPostalCode("95064");
        a.setCountry("USA");
        return a;
    }

    @Test
    @DisplayName("Saving a valid address assigns an ID")
    void saveAddress_assignsId() {
        Address addr = createAddress();

        Address saved = addressRepository.save(addr);

        assertNotNull(saved.getId());  // or getAddressId() if that is your field
    }

    @Test
    @DisplayName("findById returns the saved address")
    void findById_returnsAddress() {
        Address addr = createAddress();
        Address saved = addressRepository.save(addr);

        Address found = addressRepository.findById(saved.getId()).orElseThrow();

        assertEquals("123 Main St", found.getStreet());
        assertEquals("Santa Cruz", found.getCity());
    }

    @Test
    @DisplayName("Updating an address persists changes")
    void updateAddress_persistsChanges() {
        Address addr = createAddress();
        Address saved = addressRepository.save(addr);

        saved.setCity("Los Angeles");
        addressRepository.save(saved);

        Address reloaded = addressRepository.findById(saved.getId()).orElseThrow();
        assertEquals("Los Angeles", reloaded.getCity());
    }

    @Test
    @DisplayName("Deleting an address removes it from the database")
    void deleteAddress_removesFromDb() {
        Address addr = createAddress();
        Address saved = addressRepository.save(addr);

        addressRepository.delete(saved);

        assertTrue(addressRepository.findById(saved.getId()).isEmpty());
    }

    @Test
    @DisplayName("findAll returns all saved addresses")
    void findAll_returnsAllAddresses() {
        addressRepository.save(createAddress());
        addressRepository.save(createAddress());

        List<Address> all = addressRepository.findAll();

        assertEquals(2, all.size());
    }
}
