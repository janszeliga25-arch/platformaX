package pl.platformax.platformaxbackend.domain.account;

import jakarta.persistence.*;
import pl.platformax.platformaxbackend.domain.org.Organization;

import java.time.Instant;

@Entity
@Table(name = "org_accounts")
public class OrgAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, name = "password_hash")
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrgRole role;

    @Column(nullable = false, name = "created_at")
    private Instant createdAt;

    protected OrgAccount() {
        // for JPA
    }

    public OrgAccount(Organization organization, String email, String passwordHash, OrgRole role) {
        this.organization = organization;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public Organization getOrganization() {
        return organization;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public OrgRole getRole() {
        return role;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}