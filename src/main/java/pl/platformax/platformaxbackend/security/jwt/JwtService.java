package pl.platformax.platformaxbackend.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;
import pl.platformax.platformaxbackend.domain.account.AccountType;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

@Service
public class JwtService {

    private final JwtProperties properties;

    public JwtService(JwtProperties properties) {
        this.properties = properties;
    }

    public String generateToken(Long accountId, AccountType accountType, List<String> roles, Long orgId) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + properties.getExpirationSeconds() * 1000L);

        var builder = Jwts.builder()
                .subject(String.valueOf(accountId))
                .claim("accountId", accountId)
                .claim("accountType", accountType.name())
                .claim("roles", roles)
                .issuedAt(now)
                .expiration(expiry);

        if (orgId != null) {
            builder.claim("orgId", orgId);
        }

        return builder.signWith(secretKey()).compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey secretKey() {
        return Keys.hmacShaKeyFor(properties.getSecret().getBytes(StandardCharsets.UTF_8));
    }
}
