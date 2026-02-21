package pl.platformax.platformaxbackend.domain.email;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailRegistryRepository extends JpaRepository<EmailRegistry, Long> {

    boolean existsByEmail(String email);

    Optional<EmailRegistry> findByEmail(String email);
}