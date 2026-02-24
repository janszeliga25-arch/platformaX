package pl.platformax.platformaxbackend.api.org.auth;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.platformax.platformaxbackend.api.auth.dto.LoginRequest;
import pl.platformax.platformaxbackend.api.auth.dto.TokenResponse;
import pl.platformax.platformaxbackend.domain.account.OrgLoginService;

@RestController
@RequestMapping("/api/org/auth")
public class OrgAuthController {

    private final OrgLoginService orgLoginService;

    public OrgAuthController(OrgLoginService orgLoginService) {
        this.orgLoginService = orgLoginService;
    }

    @PostMapping("/login")
    public TokenResponse login(@RequestBody @Valid LoginRequest request) {
        String token = orgLoginService.login(request.email(), request.password());
        return new TokenResponse(token);
    }
}
