package pl.platformax.platformaxbackend.api.org.activity;

public class MissingOrganizationContextException extends RuntimeException {

    public MissingOrganizationContextException() {
        super("Organization context is missing from the authenticated principal");
    }
}
