package pl.platformax.platformaxbackend.security.testcontroller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/org")
public class OrgTestController {

    @GetMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void me() {
    }
}
