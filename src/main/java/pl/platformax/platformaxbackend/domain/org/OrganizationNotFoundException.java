package pl.platformax.platformaxbackend.domain.org;

public class OrganizationNotFoundException extends RuntimeException {

    public OrganizationNotFoundException(Long organizationId) {
        super("Organization not found with id: " + organizationId);
    }
}
