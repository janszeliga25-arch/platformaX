package pl.platformax.platformaxbackend.api.org.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record OrgRegisterRequest(
        @NotBlank String orgName,
        @NotBlank String krs,
        @Email @NotBlank String adminEmail,
        @NotBlank @Size(min = 8) String adminPassword) {}
