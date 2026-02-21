package pl.platformax.platformaxbackend.domain.org;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "organizations")
public class Organization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String krs;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrganizationStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    protected Organization() {
        // wymagane przez JPA (Hibernate)
    }

    public Organization(String name, String krs) {
        this.name = name;
        this.krs = krs;
        this.status = OrganizationStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getKrs() { return krs; }
    public OrganizationStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void verify() {
        this.status = OrganizationStatus.VERIFIED;
    }
}