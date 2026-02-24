package pl.platformax.platformaxbackend.domain.account;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.platformax.platformaxbackend.security.jwt.JwtService;

import java.util.List;

@Service
public class OrgLoginService {

    private final OrgAccountRepository orgAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public OrgLoginService(OrgAccountRepository orgAccountRepository,
                           PasswordEncoder passwordEncoder,
                           JwtService jwtService) {
        this.orgAccountRepository = orgAccountRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public String login(String email, String password) {
        String normalized = EmailNormalizer.normalize(email);
        OrgAccount account = orgAccountRepository.findByEmail(normalized)
                .orElseThrow(InvalidCredentialsException::new);
        if (!passwordEncoder.matches(password, account.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }
        List<String> roles = List.of(account.getRole().name());
        Long orgId = account.getOrganization().getId();
        return jwtService.generateToken(account.getId(), AccountType.ORG, roles, orgId);
    }
}
