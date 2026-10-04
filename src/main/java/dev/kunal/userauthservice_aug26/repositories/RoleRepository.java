package dev.kunal.userauthservice_aug26.repositories;

import dev.kunal.userauthservice_aug26.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByValue(String value);
}
