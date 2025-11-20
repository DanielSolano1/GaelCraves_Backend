package com.gaelcraves.project3.GaelCravings_Backend.Tools;

import com.gaelcraves.project3.GaelCravings_Backend.Entity.Roles;
import com.gaelcraves.project3.GaelCravings_Backend.Repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initRoles(RoleRepository roleRepository) {
        return args -> {
            if (!roleRepository.existsByRoleName("USER")) {
                Roles userRole = new Roles();
                userRole.setRoleName("USER");
                roleRepository.save(userRole);
            }

            if (!roleRepository.existsByRoleName("ADMIN")) {
                Roles adminRole = new Roles();
                adminRole.setRoleName("ADMIN");
                roleRepository.save(adminRole);
            }

            if (!roleRepository.existsByRoleName("GAEL_HIMSELF")) {
                Roles ownerRole = new Roles();
                ownerRole.setRoleName("GAEL_HIMSELF");
                roleRepository.save(ownerRole);
            }
        };
    }
}