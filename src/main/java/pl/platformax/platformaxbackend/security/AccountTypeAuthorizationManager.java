package pl.platformax.platformaxbackend.security;

import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import pl.platformax.platformaxbackend.domain.account.AccountType;

import java.util.function.Supplier;

public class AccountTypeAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

    private final AccountType requiredType;

    public AccountTypeAuthorizationManager(AccountType requiredType) {
        this.requiredType = requiredType;
    }

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication,
                                       RequestAuthorizationContext object) {
        Authentication auth = authentication.get();
        if (auth == null || !auth.isAuthenticated()) {
            return new AuthorizationDecision(false);
        }
        Object principal = auth.getPrincipal();
        if (!(principal instanceof AuthenticatedAccount account)) {
            return new AuthorizationDecision(false);
        }
        return new AuthorizationDecision(account.accountType() == requiredType);
    }
}
