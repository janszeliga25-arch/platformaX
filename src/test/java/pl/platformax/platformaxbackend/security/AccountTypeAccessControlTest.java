package pl.platformax.platformaxbackend.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pl.platformax.platformaxbackend.domain.account.AccountType;
import pl.platformax.platformaxbackend.security.jwt.JwtService;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AccountTypeAccessControlTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    private String userToken;
    private String orgToken;
    private String platformAdminToken;

    @BeforeEach
    void setUp() {
        userToken = jwtService.generateToken(1L, AccountType.USER, List.of("USER"), null);
        orgToken = jwtService.generateToken(2L, AccountType.ORG, List.of("ORG_ADMIN"), 10L);
        platformAdminToken = jwtService.generateToken(3L, AccountType.PLATFORM_ADMIN, List.of(), null);
    }

    @Test
    void publicEndpoints_doNotRequireAuthentication() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"pub-access-ctrl@example.com\",\"password\":\"12345678\"}"))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/org/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"organizationName\":\"PubOrg\",\"krs\":\"9999999901\"," +
                                "\"email\":\"pub-org-access-ctrl@example.com\",\"password\":\"12345678\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    void unauthenticated_protectedEndpoint_returns401() throws Exception {
        mockMvc.perform(get("/api/user/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("UNAUTHORIZED"));
    }

    @Test
    void userToken_canAccess_userEndpoint() throws Exception {
        mockMvc.perform(get("/api/user/me")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void userToken_cannotAccess_orgEndpoint_returns403() throws Exception {
        mockMvc.perform(get("/api/org/me")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("FORBIDDEN"));
    }

    @Test
    void orgToken_canAccess_orgEndpoint() throws Exception {
        mockMvc.perform(get("/api/org/me")
                        .header("Authorization", "Bearer " + orgToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void orgToken_cannotAccess_userEndpoint_returns403() throws Exception {
        mockMvc.perform(get("/api/user/me")
                        .header("Authorization", "Bearer " + orgToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("FORBIDDEN"));
    }

    @Test
    void userToken_cannotAccess_adminEndpoint_returns403() throws Exception {
        mockMvc.perform(get("/api/admin/ping")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("FORBIDDEN"));
    }

    @Test
    void orgToken_cannotAccess_adminEndpoint_returns403() throws Exception {
        mockMvc.perform(get("/api/admin/ping")
                        .header("Authorization", "Bearer " + orgToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("FORBIDDEN"));
    }

    @Test
    void platformAdminToken_canAccess_adminEndpoint() throws Exception {
        mockMvc.perform(get("/api/admin/ping")
                        .header("Authorization", "Bearer " + platformAdminToken))
                .andExpect(status().isNoContent());
    }
}
