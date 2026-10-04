package dev.kunal.userauthservice_aug26.repositories;

import dev.kunal.userauthservice_aug26.models.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SessionRepo extends JpaRepository<UserSession, Long> {

    Optional<UserSession> findByToken(String token);
}
