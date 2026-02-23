package pl.platformax.platformaxbackend.domain.account;

import java.util.Locale;
import java.util.Objects;

public final class EmailNormalizer {

    private EmailNormalizer() {}

    public static String normalize(String raw) {
        return Objects.requireNonNull(raw, "email must not be null").trim().toLowerCase(Locale.ROOT);
    }
}
