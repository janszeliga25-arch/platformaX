package pl.platformax.platformaxbackend.domain.account;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.platformax.platformaxbackend.security.jwt.JwtService;

import java.util.List;

@Service
public class UserLoginService {

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserLoginService(UserAccountRepository userAccountRepository,
                            PasswordEncoder passwordEncoder,
                            JwtService jwtService) {
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public String login(String email, String password) {
        String normalized = EmailNormalizer.normalize(email);
        UserAccount account = userAccountRepository.findByEmail(normalized)
                .orElseThrow(InvalidCredentialsException::new);
        if (!passwordEncoder.matches(password, account.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }
        List<String> roles = List.of(account.getRole().name());
        return jwtService.generateToken(account.getId(), AccountType.USER, roles, null);
    }
}
