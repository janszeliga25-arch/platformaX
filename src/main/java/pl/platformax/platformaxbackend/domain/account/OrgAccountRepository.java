package pl.platformax.platformaxbackend.domain.account;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrgAccountRepository extends JpaRepository<OrgAccount, Long> {
    boolean existsByEmail(String email);
    Optional<OrgAccount> findByEmail(String email);
}