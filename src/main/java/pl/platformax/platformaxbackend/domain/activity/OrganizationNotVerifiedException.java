package pl.platformax.platformaxbackend.domain.activity;

public class OrganizationNotVerifiedException extends RuntimeException {

    public OrganizationNotVerifiedException(Long organizationId) {
        super("Organization is not verified: " + organizationId);
    }
}
