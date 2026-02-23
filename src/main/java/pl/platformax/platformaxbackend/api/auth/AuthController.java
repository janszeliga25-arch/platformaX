package pl.platformax.platformaxbackend.api.auth;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pl.platformax.platformaxbackend.api.auth.dto.UserRegisterRequest;
import pl.platformax.platformaxbackend.api.auth.dto.UserRegisterResponse;
import pl.platformax.platformaxbackend.domain.account.UserRegistrationService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRegistrationService userRegistrationService;

    public AuthController(UserRegistrationService userRegistrationService) {
        this.userRegistrationService = userRegistrationService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserRegisterResponse register(@RequestBody @Valid UserRegisterRequest request) {
        Long accountId = userRegistrationService.registerUser(request.email(), request.password());
        return new UserRegisterResponse(accountId);
    }
}
