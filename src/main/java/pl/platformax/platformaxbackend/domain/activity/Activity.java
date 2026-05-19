package pl.platformax.platformaxbackend.domain.activity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "activities")
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long organizationId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActivityType activityType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActivityStatus status;

    @Column(nullable = false)
    private LocalDateTime startDateTime;

    @Column
    private LocalDateTime endDateTime;

    @Column
    private String locationName;

    @Column
    private String city;

    @Column(nullable = false)
    private boolean online;

    @Column
    private Integer maxParticipants;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    protected Activity() {
        // required by JPA (Hibernate)
    }

    public Activity(Long organizationId, String title, String description,
                    ActivityType activityType, LocalDateTime startDateTime) {
        this.organizationId = organizationId;
        this.title = title;
        this.description = description;
        this.activityType = activityType;
        this.startDateTime = startDateTime;
        this.status = ActivityStatus.DRAFT;
        this.online = false;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    public Long getId() { return id; }
    public Long getOrganizationId() { return organizationId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public ActivityType getActivityType() { return activityType; }
    public ActivityStatus getStatus() { return status; }
    public LocalDateTime getStartDateTime() { return startDateTime; }
    public LocalDateTime getEndDateTime() { return endDateTime; }
    public String getLocationName() { return locationName; }
    public String getCity() { return city; }
    public boolean isOnline() { return online; }
    public Integer getMaxParticipants() { return maxParticipants; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public void setTitle(String title) {
        this.title = title;
        this.updatedAt = LocalDateTime.now();
    }

    public void setDescription(String description) {
        this.description = description;
        this.updatedAt = LocalDateTime.now();
    }

    public void setActivityType(ActivityType activityType) {
        this.activityType = activityType;
        this.updatedAt = LocalDateTime.now();
    }

    public void setStatus(ActivityStatus status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }

    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
        this.updatedAt = LocalDateTime.now();
    }

    public void setEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
        this.updatedAt = LocalDateTime.now();
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
        this.updatedAt = LocalDateTime.now();
    }

    public void setCity(String city) {
        this.city = city;
        this.updatedAt = LocalDateTime.now();
    }

    public void setOnline(boolean online) {
        this.online = online;
        this.updatedAt = LocalDateTime.now();
    }

    public void setMaxParticipants(Integer maxParticipants) {
        this.maxParticipants = maxParticipants;
        this.updatedAt = LocalDateTime.now();
    }
}
