package pl.platformax.platformaxbackend.domain.activity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ActivityRepository extends JpaRepository<Activity, Long> {
    Optional<Activity> findByIdAndOrganizationId(Long id, Long organizationId);
}
