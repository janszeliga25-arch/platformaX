package pl.platformax.platformaxbackend.api.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import pl.platformax.platformaxbackend.api.auth.dto.TokenResponse;
import pl.platformax.platformaxbackend.security.jwt.JwtService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtService jwtService;

    @Test
    void userLogin_returnsToken() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"login@example.com\",\"password\":\"password123\"}"))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"login@example.com\",\"password\":\"password123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isString());
    }

    @Test
    void userLogin_tokenHasCorrectClaims() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"claims@example.com\",\"password\":\"password123\"}"))
                .andExpect(status().isCreated());

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"claims@example.com\",\"password\":\"password123\"}"))
                .andExpect(status().isOk())
                .andReturn();

        String body = result.getResponse().getContentAsString();
        String token = objectMapper.readValue(body, TokenResponse.class).token();

        var claims = jwtService.parseToken(token);
        assertThat(claims.get("accountType")).isEqualTo("USER");
        assertThat(claims.get("roles")).asList().containsExactly("USER");
        assertThat(claims.get("orgId")).isNull();
        assertThat(claims.get("accountId")).isNotNull();
        assertThat(claims.getSubject()).isEqualTo(String.valueOf(claims.get("accountId")));
    }

    @Test
    void userLogin_wrongPassword_returns401() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"wrongpw@example.com\",\"password\":\"password123\"}"))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"wrongpw@example.com\",\"password\":\"wrongpassword\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("INVALID_CREDENTIALS"));
    }

    @Test
    void userLogin_unknownEmail_returns401() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"nobody@example.com\",\"password\":\"password123\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("INVALID_CREDENTIALS"));
    }

    @Test
    void orgLogin_tokenHasOrgIdAndOrgType() throws Exception {
        mockMvc.perform(post("/api/org/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"organizationName\":\"TestOrg\",\"krs\":\"0000000099\"," +
                                "\"email\":\"orglogin@example.com\",\"password\":\"password123\"}"))
                .andExpect(status().isCreated());

        MvcResult result = mockMvc.perform(post("/api/org/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"orglogin@example.com\",\"password\":\"password123\"}"))
                .andExpect(status().isOk())
                .andReturn();

        String body = result.getResponse().getContentAsString();
        String token = objectMapper.readValue(body, TokenResponse.class).token();

        var claims = jwtService.parseToken(token);
        assertThat(claims.get("accountType")).isEqualTo("ORG");
        assertThat(claims.get("roles")).asList().containsExactly("ORG_ADMIN");
        assertThat(claims.get("orgId")).isNotNull();
        assertThat(claims.get("accountId")).isNotNull();
        assertThat(claims.getSubject()).isEqualTo(String.valueOf(claims.get("accountId")));
    }
}
