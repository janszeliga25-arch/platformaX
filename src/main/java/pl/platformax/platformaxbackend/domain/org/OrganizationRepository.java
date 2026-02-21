package pl.platformax.platformaxbackend.domain.org;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrganizationRepository extends JpaRepository<Organization, Long> {
    Optional<Organization> findByKrs(String krs);
    boolean existsByKrs(String krs);
}