package pl.platformax.platformaxbackend.domain.account;

public class KrsAlreadyUsedException extends RuntimeException {
    public KrsAlreadyUsedException() {
        super("KRS already used");
    }
}
