package pl.platformax.platformaxbackend.domain.account;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.platformax.platformaxbackend.domain.email.EmailRegistry;
import pl.platformax.platformaxbackend.domain.email.EmailRegistryRepository;
import pl.platformax.platformaxbackend.domain.org.Organization;
import pl.platformax.platformaxbackend.domain.org.OrganizationRepository;

import java.time.Instant;

@Service
public class OrgRegistrationService {

    private final EmailRegistryRepository emailRegistryRepository;
    private final OrganizationRepository organizationRepository;
    private final OrgAccountRepository orgAccountRepository;
    private final PasswordEncoder passwordEncoder;

    public OrgRegistrationService(EmailRegistryRepository emailRegistryRepository,
                                  OrganizationRepository organizationRepository,
                                  OrgAccountRepository orgAccountRepository,
                                  PasswordEncoder passwordEncoder) {
        this.emailRegistryRepository = emailRegistryRepository;
        this.organizationRepository = organizationRepository;
        this.orgAccountRepository = orgAccountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public record OrgRegistrationResult(Long orgId, Long accountId) {}

    @Transactional
    public OrgRegistrationResult registerOrganizationWithAdmin(String orgName, String krs,
                                                               String adminEmail, String adminPassword) {
        String normalized = EmailNormalizer.normalize(adminEmail);
        try {
            emailRegistryRepository.saveAndFlush(new EmailRegistry(normalized, Instant.now()));
        } catch (DataIntegrityViolationException e) {
            throw new EmailAlreadyUsedException();
        }
        Organization org;
        try {
            org = organizationRepository.saveAndFlush(new Organization(orgName, krs));
        } catch (DataIntegrityViolationException e) {
            throw new KrsAlreadyUsedException();
        }
        OrgAccount account = orgAccountRepository.save(
                new OrgAccount(org, normalized, passwordEncoder.encode(adminPassword), OrgRole.ORG_ADMIN));
        return new OrgRegistrationResult(org.getId(), account.getId());
    }
}
