package pl.platformax.platformaxbackend.api.org;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class OrgControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void registerOrg_returns201WithIds() throws Exception {
        mockMvc.perform(post("/api/org/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"orgName\":\"Acme\",\"krs\":\"0000000001\"," +
                                "\"adminEmail\":\"admin@acme.com\",\"adminPassword\":\"12345678\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orgId").isNumber())
                .andExpect(jsonPath("$.accountId").isNumber());
    }

    @Test
    void registerOrg_duplicateKrs_returns409() throws Exception {
        mockMvc.perform(post("/api/org/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"orgName\":\"Acme\",\"krs\":\"0000000002\"," +
                                "\"adminEmail\":\"admin1@acme.com\",\"adminPassword\":\"12345678\"}"))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/org/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"orgName\":\"Acme2\",\"krs\":\"0000000002\"," +
                                "\"adminEmail\":\"admin2@acme.com\",\"adminPassword\":\"12345678\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("KRS_ALREADY_USED"));
    }

    @Test
    void registerOrg_duplicateAdminEmail_returns409() throws Exception {
        mockMvc.perform(post("/api/org/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"orgName\":\"Acme\",\"krs\":\"0000000003\"," +
                                "\"adminEmail\":\"shared@acme.com\",\"adminPassword\":\"12345678\"}"))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/org/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"orgName\":\"Acme2\",\"krs\":\"0000000004\"," +
                                "\"adminEmail\":\"shared@acme.com\",\"adminPassword\":\"12345678\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("EMAIL_ALREADY_USED"));
    }

    @Test
    void registerOrg_missingField_returns400() throws Exception {
        mockMvc.perform(post("/api/org/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"orgName\":\"Acme\",\"krs\":\"0000000005\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));
    }
}
