package pl.platformax.platformaxbackend.api.org;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pl.platformax.platformaxbackend.api.auth.dto.LoginRequest;
import pl.platformax.platformaxbackend.api.auth.dto.TokenResponse;
import pl.platformax.platformaxbackend.api.org.dto.OrgRegisterRequest;
import pl.platformax.platformaxbackend.api.org.dto.OrgRegisterResponse;
import pl.platformax.platformaxbackend.domain.account.OrgLoginService;
import pl.platformax.platformaxbackend.domain.account.OrgRegistrationService;

@RestController
@RequestMapping("/api/org")
public class OrgController {

    private final OrgRegistrationService orgRegistrationService;
    private final OrgLoginService orgLoginService;

    public OrgController(OrgRegistrationService orgRegistrationService,
                         OrgLoginService orgLoginService) {
        this.orgRegistrationService = orgRegistrationService;
        this.orgLoginService = orgLoginService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public OrgRegisterResponse register(@RequestBody @Valid OrgRegisterRequest request) {
        OrgRegistrationService.OrgRegistrationResult result = orgRegistrationService
                .registerOrganizationWithAdmin(request.organizationName(), request.krs(),
                        request.email(), request.password());
        return new OrgRegisterResponse(result.orgId(), result.accountId());
    }

    @PostMapping("/login")
    public TokenResponse login(@RequestBody @Valid LoginRequest request) {
        String token = orgLoginService.login(request.email(), request.password());
        return new TokenResponse(token);
    }
}
