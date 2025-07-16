package com.ops.hunting.common.entity;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "threat_intelligence")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class ThreatIntel extends BaseEntity {
    
    @NotBlank(message = "IOC type is required")
    @Column(name = "ioc_type", nullable = false)
    private String iocType;
    
    @NotBlank(message = "IOC value is required")
    @Column(name = "ioc_value", nullable = false, length = 1000)
    private String iocValue;
    
    @NotBlank(message = "Threat level is required")
    @Column(name = "threat_level", nullable = false)
    private String threatLevel;
    
    @NotBlank(message = "Source is required")
    @Column(name = "source", nullable = false)
    private String source;
    
    @Min(value = 0, message = "Confidence must be between 0 and 100")
    @Max(value = 100, message = "Confidence must be between 0 and 100")
    @Column(name = "confidence")
    private Integer confidence;
    
    @Column(name = "description", length = 2000)
    private String description;
    
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;
    
    @Column(name = "first_seen")
    private LocalDateTime firstSeen;
    
    @Column(name = "last_seen")
    private LocalDateTime lastSeen;
    
    @Column(name = "tags")
    private String tags;
    
    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;
    
    @Column(name = "external_id")
    private String externalId;
    
    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }
    
    public boolean isHighConfidence() {
        return confidence != null && confidence >= 85;
    }
}
