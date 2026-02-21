package pl.platformax.platformaxbackend.domain.org;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
class OrganizationRepositoryTest {

    @Autowired
    private OrganizationRepository repository;

    @Test
    void shouldEnforceUniqueKrsConstraint() {
        repository.saveAndFlush(new Organization("Org A", "0000123456"));

        assertThrows(DataIntegrityViolationException.class, () ->
                repository.saveAndFlush(new Organization("Org B", "0000123456"))
        );
    }
}