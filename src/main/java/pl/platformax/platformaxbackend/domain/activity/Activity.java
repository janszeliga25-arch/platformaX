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
                    ActivityType activityType, LocalDateTime startDateTime,
                    LocalDateTime endDateTime) {
        if (organizationId == null) {
            throw new IllegalArgumentException("organizationId must not be null");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("title must not be blank");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("description must not be blank");
        }
        if (activityType == null) {
            throw new IllegalArgumentException("activityType must not be null");
        }
        if (startDateTime == null) {
            throw new IllegalArgumentException("startDateTime must not be null");
        }
        if (endDateTime != null && !endDateTime.isAfter(startDateTime)) {
            throw new IllegalArgumentException("endDateTime must be after startDateTime");
        }
        this.organizationId = organizationId;
        this.title = title;
        this.description = description;
        this.activityType = activityType;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
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

    public void publish() {
        if (this.status != ActivityStatus.DRAFT) {
            throw new ActivityCannotBePublishedException(this.id, this.status);
        }
        this.status = ActivityStatus.PUBLISHED;
        this.updatedAt = LocalDateTime.now();
    }
}
