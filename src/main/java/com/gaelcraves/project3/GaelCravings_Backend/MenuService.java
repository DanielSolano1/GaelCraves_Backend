package com.gaelcraves.project3.GaelCravings_Backend;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class MenuService {

    private final MenuRepository repo;

    public MenuService(MenuRepository repo) {
        this.repo = repo;
    }

    @Transactional(readOnly = true)
    public List<Menu> findAll() {
        return repo.findAll();
    }

    @Transactional(readOnly = true)
    public Menu findById(Integer id) {
        return repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Menu not found: " + id));
    }

    public Menu create(Menu menu) {
        menu.setMenuId(null);
        return repo.save(menu);
    }

    public Menu update(Integer id, Menu updated) {
        Menu existing = findById(id);
        existing.setName(updated.getName());
        existing.setFoodItems(updated.getFoodItems());
        return repo.save(existing);
    }

    public void delete(Integer id) {
        if (!repo.existsById(id)) {
            throw new IllegalArgumentException("Menu not found: " + id);
        }
        repo.deleteById(id);
    }
}
