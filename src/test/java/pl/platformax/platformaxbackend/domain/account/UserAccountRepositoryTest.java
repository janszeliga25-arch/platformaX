package pl.platformax.platformaxbackend.domain.account;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
class UserAccountRepositoryTest {

    @Autowired
    private UserAccountRepository repository;

    @Test
    void shouldEnforceUniqueEmailConstraint() {
        repository.saveAndFlush(new UserAccount("user@example.com", "{bcrypt}hash"));

        assertThrows(DataIntegrityViolationException.class, () ->
                repository.saveAndFlush(new UserAccount("user@example.com", "{bcrypt}hash2"))
        );
    }
}