package pl.platformax.platformaxbackend.api.org;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pl.platformax.platformaxbackend.api.org.dto.OrgRegisterRequest;
import pl.platformax.platformaxbackend.api.org.dto.OrgRegisterResponse;
import pl.platformax.platformaxbackend.domain.account.OrgRegistrationService;

@RestController
@RequestMapping("/api/org")
public class OrgController {

    private final OrgRegistrationService orgRegistrationService;

    public OrgController(OrgRegistrationService orgRegistrationService) {
        this.orgRegistrationService = orgRegistrationService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public OrgRegisterResponse register(@RequestBody @Valid OrgRegisterRequest request) {
        OrgRegistrationService.OrgRegistrationResult result = orgRegistrationService
                .registerOrganizationWithAdmin(request.orgName(), request.krs(),
                        request.adminEmail(), request.adminPassword());
        return new OrgRegisterResponse(result.orgId(), result.accountId());
    }
}
