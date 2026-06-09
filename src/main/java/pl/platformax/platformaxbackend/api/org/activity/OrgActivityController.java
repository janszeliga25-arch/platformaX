package pl.platformax.platformaxbackend.api.org.activity;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pl.platformax.platformaxbackend.api.org.activity.dto.CreateActivityRequest;
import pl.platformax.platformaxbackend.api.org.activity.dto.CreateActivityResponse;
import pl.platformax.platformaxbackend.domain.activity.Activity;
import pl.platformax.platformaxbackend.domain.activity.ActivityCreationService;
import pl.platformax.platformaxbackend.security.AuthenticatedAccount;

@RestController
@RequestMapping("/api/org/activities")
public class OrgActivityController {

    private final ActivityCreationService activityCreationService;

    public OrgActivityController(ActivityCreationService activityCreationService) {
        this.activityCreationService = activityCreationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateActivityResponse create(
            @RequestBody @Valid CreateActivityRequest request,
            @AuthenticationPrincipal AuthenticatedAccount principal) {
        Long orgId = principal.orgId();
        if (orgId == null) {
            throw new MissingOrganizationContextException();
        }
        Activity activity = activityCreationService.createActivity(
                orgId,
                request.title(),
                request.description(),
                request.activityType(),
                request.startDateTime(),
                request.endDateTime()
        );
        return CreateActivityResponse.from(activity);
    }
}
