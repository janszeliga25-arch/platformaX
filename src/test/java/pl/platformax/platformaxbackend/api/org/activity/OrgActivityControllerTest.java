package pl.platformax.platformaxbackend.api.org.activity;

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
import pl.platformax.platformaxbackend.domain.account.AccountType;
import pl.platformax.platformaxbackend.domain.org.OrganizationRepository;
import pl.platformax.platformaxbackend.domain.org.OrganizationVerificationService;
import pl.platformax.platformaxbackend.security.jwt.JwtService;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class OrgActivityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private OrganizationVerificationService organizationVerificationService;

    @Autowired
    private OrganizationRepository organizationRepository;

    private static final String VALID_BODY =
            "{\"title\":\"Training\",\"description\":\"A great training\"," +
            "\"activityType\":\"TRAINING\"," +
            "\"startDateTime\":\"2026-06-01T10:00:00\"," +
            "\"endDateTime\":\"2026-06-01T12:00:00\"}";

    // ── helpers ──────────────────────────────────────────────────────────────

    private String registerAndGetOrgToken(String orgName, String krs, String email, String password)
            throws Exception {
        mockMvc.perform(post("/api/org/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"organizationName\":\"" + orgName + "\",\"krs\":\"" + krs + "\"," +
                                 "\"email\":\"" + email + "\",\"password\":\"" + password + "\"}"))
                .andExpect(status().isCreated());

        MvcResult loginResult = mockMvc.perform(post("/api/org/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}"))
                .andExpect(status().isOk())
                .andReturn();

        String body = loginResult.getResponse().getContentAsString();
        return objectMapper.readValue(body, TokenResponse.class).token();
    }

    private String registerVerifyAndGetOrgToken(String orgName, String krs, String email, String password)
            throws Exception {
        // Register returns orgId in response
        MvcResult registerResult = mockMvc.perform(post("/api/org/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"organizationName\":\"" + orgName + "\",\"krs\":\"" + krs + "\"," +
                                 "\"email\":\"" + email + "\",\"password\":\"" + password + "\"}"))
                .andExpect(status().isCreated())
                .andReturn();

        String registerBody = registerResult.getResponse().getContentAsString();
        long orgId = objectMapper.readTree(registerBody).get("orgId").asLong();

        organizationVerificationService.verifyOrganization(orgId);

        MvcResult loginResult = mockMvc.perform(post("/api/org/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}"))
                .andExpect(status().isOk())
                .andReturn();

        String loginBody = loginResult.getResponse().getContentAsString();
        return objectMapper.readValue(loginBody, TokenResponse.class).token();
    }

    private long createActivityAndGetId(String token) throws Exception {
        MvcResult createResult = mockMvc.perform(post("/api/org/activities")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isCreated())
                .andReturn();
        return objectMapper.readTree(createResult.getResponse().getContentAsString()).get("id").asLong();
    }

    // ── test cases ────────────────────────────────────────────────────────────

    @Test
    void createActivity_verifiedOrg_returns201() throws Exception {
        String token = registerVerifyAndGetOrgToken(
                "OrgA", "0000000001", "orgA@example.com", "password123");

        mockMvc.perform(post("/api/org/activities")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isCreated());
    }

    @Test
    void createActivity_responseContainsExpectedFields() throws Exception {
        String token = registerVerifyAndGetOrgToken(
                "OrgB", "0000000002", "orgB@example.com", "password123");

        // parse orgId from JWT claims
        long orgIdFromJwt = ((Number) jwtService.parseToken(token).get("orgId")).longValue();

        mockMvc.perform(post("/api/org/activities")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.organizationId").value(orgIdFromJwt))
                .andExpect(jsonPath("$.title").value("Training"))
                .andExpect(jsonPath("$.description").value("A great training"))
                .andExpect(jsonPath("$.activityType").value("TRAINING"))
                .andExpect(jsonPath("$.status").value("DRAFT"))
                .andExpect(jsonPath("$.startDateTime").value("2026-06-01T10:00:00"))
                .andExpect(jsonPath("$.endDateTime").value("2026-06-01T12:00:00"))
                .andExpect(jsonPath("$.online").value(false))
                .andExpect(jsonPath("$.createdAt").isString());
    }

    @Test
    void createActivity_organizationIdInBodyIsIgnored() throws Exception {
        String token = registerVerifyAndGetOrgToken(
                "OrgC", "0000000003", "orgC@example.com", "password123");

        long orgIdFromJwt = ((Number) jwtService.parseToken(token).get("orgId")).longValue();

        // Send organizationId: 99999 in body — should be ignored
        String bodyWithOrgId =
                "{\"organizationId\":99999," +
                "\"title\":\"Training\",\"description\":\"A great training\"," +
                "\"activityType\":\"TRAINING\"," +
                "\"startDateTime\":\"2026-06-01T10:00:00\"," +
                "\"endDateTime\":\"2026-06-01T12:00:00\"}";

        mockMvc.perform(post("/api/org/activities")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyWithOrgId))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.organizationId").value(orgIdFromJwt));
    }

    @Test
    void createActivity_withoutEndDateTime_returns201AndEndDateTimeIsNull() throws Exception {
        String token = registerVerifyAndGetOrgToken(
                "OrgD", "0000000004", "orgD@example.com", "password123");

        String bodyWithoutEnd =
                "{\"title\":\"Training\",\"description\":\"A great training\"," +
                "\"activityType\":\"TRAINING\"," +
                "\"startDateTime\":\"2026-06-01T10:00:00\"}";

        mockMvc.perform(post("/api/org/activities")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyWithoutEnd))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.endDateTime").doesNotExist());
    }

    @Test
    void createActivity_endAfterStart_returns201() throws Exception {
        String token = registerVerifyAndGetOrgToken(
                "OrgE", "0000000005", "orgE@example.com", "password123");

        mockMvc.perform(post("/api/org/activities")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isCreated());
    }

    @Test
    void createActivity_pendingOrg_returns422() throws Exception {
        String token = registerAndGetOrgToken(
                "OrgF", "0000000006", "orgF@example.com", "password123");

        mockMvc.perform(post("/api/org/activities")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.error").value("ORGANIZATION_NOT_VERIFIED"));
    }

    @Test
    void createActivity_missingTitle_returns400() throws Exception {
        String token = registerVerifyAndGetOrgToken(
                "OrgG", "0000000007", "orgG@example.com", "password123");

        String body =
                "{\"description\":\"A great training\"," +
                "\"activityType\":\"TRAINING\"," +
                "\"startDateTime\":\"2026-06-01T10:00:00\"}";

        mockMvc.perform(post("/api/org/activities")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));
    }

    @Test
    void createActivity_blankTitle_returns400() throws Exception {
        String token = registerVerifyAndGetOrgToken(
                "OrgH", "0000000008", "orgH@example.com", "password123");

        String body =
                "{\"title\":\"   \"," +
                "\"description\":\"A great training\"," +
                "\"activityType\":\"TRAINING\"," +
                "\"startDateTime\":\"2026-06-01T10:00:00\"}";

        mockMvc.perform(post("/api/org/activities")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));
    }

    @Test
    void createActivity_missingDescription_returns400() throws Exception {
        String token = registerVerifyAndGetOrgToken(
                "OrgI", "0000000009", "orgI@example.com", "password123");

        String body =
                "{\"title\":\"Training\"," +
                "\"activityType\":\"TRAINING\"," +
                "\"startDateTime\":\"2026-06-01T10:00:00\"}";

        mockMvc.perform(post("/api/org/activities")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));
    }

    @Test
    void createActivity_missingActivityType_returns400() throws Exception {
        String token = registerVerifyAndGetOrgToken(
                "OrgJ", "0000000010", "orgJ@example.com", "password123");

        String body =
                "{\"title\":\"Training\"," +
                "\"description\":\"A great training\"," +
                "\"startDateTime\":\"2026-06-01T10:00:00\"}";

        mockMvc.perform(post("/api/org/activities")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));
    }

    @Test
    void createActivity_invalidActivityType_returns400() throws Exception {
        String token = registerVerifyAndGetOrgToken(
                "OrgK", "0000000011", "orgK@example.com", "password123");

        String body =
                "{\"title\":\"Training\"," +
                "\"description\":\"A great training\"," +
                "\"activityType\":\"INVALID_TYPE\"," +
                "\"startDateTime\":\"2026-06-01T10:00:00\"}";

        mockMvc.perform(post("/api/org/activities")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));
    }

    @Test
    void createActivity_missingStartDateTime_returns400() throws Exception {
        String token = registerVerifyAndGetOrgToken(
                "OrgL", "0000000012", "orgL@example.com", "password123");

        String body =
                "{\"title\":\"Training\"," +
                "\"description\":\"A great training\"," +
                "\"activityType\":\"TRAINING\"}";

        mockMvc.perform(post("/api/org/activities")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));
    }

    @Test
    void createActivity_endBeforeStart_returns400() throws Exception {
        String token = registerVerifyAndGetOrgToken(
                "OrgM", "0000000013", "orgM@example.com", "password123");

        String body =
                "{\"title\":\"Training\"," +
                "\"description\":\"A great training\"," +
                "\"activityType\":\"TRAINING\"," +
                "\"startDateTime\":\"2026-06-01T10:00:00\"," +
                "\"endDateTime\":\"2026-06-01T09:00:00\"}";

        mockMvc.perform(post("/api/org/activities")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));
    }

    @Test
    void createActivity_endEqualToStart_returns400() throws Exception {
        String token = registerVerifyAndGetOrgToken(
                "OrgN", "0000000014", "orgN@example.com", "password123");

        String body =
                "{\"title\":\"Training\"," +
                "\"description\":\"A great training\"," +
                "\"activityType\":\"TRAINING\"," +
                "\"startDateTime\":\"2026-06-01T10:00:00\"," +
                "\"endDateTime\":\"2026-06-01T10:00:00\"}";

        mockMvc.perform(post("/api/org/activities")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));
    }

    @Test
    void createActivity_userToken_returns403() throws Exception {
        String userToken = jwtService.generateToken(100L, AccountType.USER, List.of("USER"), null);

        mockMvc.perform(post("/api/org/activities")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("FORBIDDEN"));
    }

    @Test
    void createActivity_noAuthorizationHeader_returns401() throws Exception {
        mockMvc.perform(post("/api/org/activities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void publishActivity_ownedDraft_returns200AndPublishedStatus() throws Exception {
        String token = registerVerifyAndGetOrgToken(
                "OrgO", "0000000015", "orgO@example.com", "password123");
        long activityId = createActivityAndGetId(token);

        mockMvc.perform(post("/api/org/activities/" + activityId + "/publish")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(activityId))
                .andExpect(jsonPath("$.status").value("PUBLISHED"));
    }

    @Test
    void publishActivity_activityFromAnotherOrganization_returns404() throws Exception {
        String ownerToken = registerVerifyAndGetOrgToken(
                "OrgP", "0000000016", "orgP@example.com", "password123");
        long activityId = createActivityAndGetId(ownerToken);

        String anotherOrgToken = registerVerifyAndGetOrgToken(
                "OrgQ", "0000000017", "orgQ@example.com", "password123");

        mockMvc.perform(post("/api/org/activities/" + activityId + "/publish")
                        .header("Authorization", "Bearer " + anotherOrgToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("ACTIVITY_NOT_FOUND"));
    }

    @Test
    void publishActivity_whenAlreadyPublished_returns422() throws Exception {
        String token = registerVerifyAndGetOrgToken(
                "OrgR", "0000000018", "orgR@example.com", "password123");
        long activityId = createActivityAndGetId(token);

        mockMvc.perform(post("/api/org/activities/" + activityId + "/publish")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/org/activities/" + activityId + "/publish")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.error").value("ACTIVITY_CANNOT_BE_PUBLISHED"));
    }
}
