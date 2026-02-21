package pl.platformax.platformaxbackend.domain.email;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
class EmailRegistryRepositoryTest {

    @Autowired
    private EmailRegistryRepository repository;

    @Test
    void shouldEnforceUniqueEmailConstraint() {
        repository.save(new EmailRegistry("test@example.com", Instant.now()));

        assertThrows(RuntimeException.class, () ->
                repository.saveAndFlush(new EmailRegistry("test@example.com", Instant.now()))
        );
    }
}