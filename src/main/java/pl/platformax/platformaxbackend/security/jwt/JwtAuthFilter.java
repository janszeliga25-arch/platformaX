package pl.platformax.platformaxbackend.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import pl.platformax.platformaxbackend.domain.account.AccountType;
import pl.platformax.platformaxbackend.security.AuthenticatedAccount;

import java.io.IOException;
import java.util.List;

public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthFilter.class);

    private final JwtService jwtService;

    public JwtAuthFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                Claims claims = jwtService.parseToken(token);

                Object rawAccountId = claims.get("accountId");
                String rawAccountType = claims.get("accountType", String.class);
                if (rawAccountId == null || rawAccountType == null) {
                    log.debug("JWT token missing required claims");
                    filterChain.doFilter(request, response);
                    return;
                }

                Long accountId = ((Number) rawAccountId).longValue();
                AccountType accountType = AccountType.valueOf(rawAccountType);
                List<?> rawRoles = claims.get("roles", List.class);
                List<String> roles = rawRoles == null ? List.of() :
                        rawRoles.stream().map(Object::toString).toList();
                Number rawOrgId = (Number) claims.get("orgId");
                Long orgId = rawOrgId != null ? rawOrgId.longValue() : null;

                List<SimpleGrantedAuthority> authorities = roles.stream()
                        .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
                        .toList();

                AuthenticatedAccount principal = new AuthenticatedAccount(accountId, accountType, roles, orgId);
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(principal, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (JwtException | IllegalArgumentException | ClassCastException e) {
                log.debug("Invalid JWT token: {}", e.getMessage());
            }
        }
        filterChain.doFilter(request, response);
    }
}
