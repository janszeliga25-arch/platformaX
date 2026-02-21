package pl.platformax.platformaxbackend.domain.email;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "email_registry")
public class EmailRegistry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private Instant reservedAt;

    protected EmailRegistry() {
        // for JPA
    }

    public EmailRegistry(String email, Instant reservedAt) {
        this.email = email;
        this.reservedAt = reservedAt;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public Instant getReservedAt() {
        return reservedAt;
    }
}