package pl.platformax.platformaxbackend.security.jwt;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import pl.platformax.platformaxbackend.domain.account.AccountType;
import pl.platformax.platformaxbackend.security.AuthenticatedAccount;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JwtAuthFilterTest {

    private JwtService jwtService;
    private JwtAuthFilter filter;

    @BeforeEach
    void setUp() {
        jwtService = mock(JwtService.class);
        filter = new JwtAuthFilter(jwtService);
        SecurityContextHolder.clearContext();
    }

    @Test
    void validUserToken_setsPrincipalWithCorrectFields() throws Exception {
        Claims claims = mock(Claims.class);
        when(claims.get("accountId")).thenReturn(42);
        when(claims.get("accountType", String.class)).thenReturn("USER");
        when(claims.get("roles", List.class)).thenReturn(List.of("USER"));
        when(claims.get("orgId")).thenReturn(null);
        when(jwtService.parseToken("valid.user.token")).thenReturn(claims);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer valid.user.token");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilterInternal(request, response, chain);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNotNull();
        assertThat(auth.getPrincipal()).isInstanceOf(AuthenticatedAccount.class);

        AuthenticatedAccount principal = (AuthenticatedAccount) auth.getPrincipal();
        assertThat(principal.accountId()).isEqualTo(42L);
        assertThat(principal.accountType()).isEqualTo(AccountType.USER);
        assertThat(principal.roles()).containsExactly("USER");
        assertThat(principal.orgId()).isNull();
        assertThat(auth.getAuthorities())
                .extracting(Object::toString)
                .containsExactly("ROLE_USER");
    }

    @Test
    void validOrgToken_setsPrincipalWithOrgIdAndOrgRole() throws Exception {
        Claims claims = mock(Claims.class);
        when(claims.get("accountId")).thenReturn(7);
        when(claims.get("accountType", String.class)).thenReturn("ORG");
        when(claims.get("roles", List.class)).thenReturn(List.of("ORG_ADMIN"));
        when(claims.get("orgId")).thenReturn(99);
        when(jwtService.parseToken("valid.org.token")).thenReturn(claims);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer valid.org.token");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilterInternal(request, response, chain);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNotNull();
        assertThat(auth.getPrincipal()).isInstanceOf(AuthenticatedAccount.class);

        AuthenticatedAccount principal = (AuthenticatedAccount) auth.getPrincipal();
        assertThat(principal.accountId()).isEqualTo(7L);
        assertThat(principal.accountType()).isEqualTo(AccountType.ORG);
        assertThat(principal.roles()).containsExactly("ORG_ADMIN");
        assertThat(principal.orgId()).isEqualTo(99L);
        assertThat(auth.getAuthorities())
                .extracting(Object::toString)
                .containsExactly("ROLE_ORG_ADMIN");
    }

    @Test
    void noAuthorizationHeader_doesNotSetAuthentication() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilterInternal(request, response, chain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }
}
