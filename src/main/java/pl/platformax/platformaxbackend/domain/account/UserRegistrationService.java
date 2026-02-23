package pl.platformax.platformaxbackend.domain.account;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.platformax.platformaxbackend.domain.email.EmailRegistry;
import pl.platformax.platformaxbackend.domain.email.EmailRegistryRepository;

import java.time.Instant;

@Service
public class UserRegistrationService {

    private final EmailRegistryRepository emailRegistryRepository;
    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;

    public UserRegistrationService(EmailRegistryRepository emailRegistryRepository,
                                   UserAccountRepository userAccountRepository,
                                   PasswordEncoder passwordEncoder) {
        this.emailRegistryRepository = emailRegistryRepository;
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Long registerUser(String email, String password) {
        String normalized = EmailNormalizer.normalize(email);
        try {
            emailRegistryRepository.saveAndFlush(new EmailRegistry(normalized, Instant.now()));
        } catch (DataIntegrityViolationException e) {
            throw new EmailAlreadyUsedException();
        }
        UserAccount account = userAccountRepository.save(
                new UserAccount(normalized, passwordEncoder.encode(password)));
        return account.getId();
    }
}
