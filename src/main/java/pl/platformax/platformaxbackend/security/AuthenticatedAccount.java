package pl.platformax.platformaxbackend.security;

import pl.platformax.platformaxbackend.domain.account.AccountType;

import java.util.List;

public record AuthenticatedAccount(
        Long accountId,
        AccountType accountType,
        List<String> roles,
        Long orgId
) {
}
