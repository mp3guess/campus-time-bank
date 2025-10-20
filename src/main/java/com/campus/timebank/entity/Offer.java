package com.campus.timebank.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "offers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Offer {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;
    
    @NotBlank(message = "Title cannot be empty")
    @Column(nullable = false, length = 255)
    private String title;
    
    @NotBlank(message = "Description cannot be empty")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;
    
    @Positive(message = "Hours rate must be positive")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal hoursRate;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private OfferStatus status = OfferStatus.ACTIVE;
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean available = true;
    
    @OneToMany(mappedBy = "offer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Booking> bookings = new HashSet<>();
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    // Business methods
    public void deactivate() {
        this.status = OfferStatus.INACTIVE;
        this.available = false;
    }
    
    public void activate() {
        this.status = OfferStatus.ACTIVE;
        this.available = true;
    }
    
    public boolean isActive() {
        return status == OfferStatus.ACTIVE && available;
    }
    
    public enum OfferStatus {
        ACTIVE, INACTIVE, ARCHIVED
    }
}
