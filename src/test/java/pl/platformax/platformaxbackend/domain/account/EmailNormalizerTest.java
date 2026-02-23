package pl.platformax.platformaxbackend.domain.account;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EmailNormalizerTest {

    @Test
    void shouldLowercaseEmail() {
        assertEquals("test@example.com", EmailNormalizer.normalize("TEST@EXAMPLE.COM"));
    }

    @Test
    void shouldTrimEmail() {
        assertEquals("test@example.com", EmailNormalizer.normalize("  test@example.com  "));
    }

    @Test
    void shouldNormalizeUppercaseAndTrim() {
        assertEquals("user@domain.org", EmailNormalizer.normalize("  User@Domain.Org  "));
    }
}
