package pl.platformax.platformaxbackend.domain.org;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrganizationVerificationService {

    private final OrganizationRepository organizationRepository;

    public OrganizationVerificationService(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }

    @Transactional
    public void verifyOrganization(Long organizationId) {
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new OrganizationNotFoundException(organizationId));
        organization.verify();
        organizationRepository.save(organization);
    }
}
