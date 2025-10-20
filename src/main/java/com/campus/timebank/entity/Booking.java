package com.campus.timebank.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "offer_id", nullable = false)
    private Offer offer;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id", nullable = false)
    private User requester;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private BookingStatus status = BookingStatus.PENDING;
    
    @Positive(message = "Reserved hours must be positive")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal reservedHours;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal transferredHours;
    
    @Column(length = 500)
    private String cancelReason;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    @Column
    private LocalDateTime confirmedAt;
    
    @Column
    private LocalDateTime completedAt;
    
    @Column
    private LocalDateTime canceledAt;
    
    // Business methods
    public void confirm() {
        if (status != BookingStatus.PENDING) {
            throw new IllegalStateException("Only pending bookings can be confirmed");
        }
        this.status = BookingStatus.CONFIRMED;
        this.confirmedAt = LocalDateTime.now();
    }
    
    public void complete() {
        if (status != BookingStatus.CONFIRMED) {
            throw new IllegalStateException("Only confirmed bookings can be completed");
        }
        this.status = BookingStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
        this.transferredHours = reservedHours;
    }
    
    public void cancel(String reason) {
        if (status == BookingStatus.COMPLETED || status == BookingStatus.CANCELED) {
            throw new IllegalStateException("Cannot cancel completed or already canceled bookings");
        }
        this.status = BookingStatus.CANCELED;
        this.cancelReason = reason;
        this.canceledAt = LocalDateTime.now();
    }
    
    public boolean canBeCanceled() {
        return status != BookingStatus.COMPLETED && status != BookingStatus.CANCELED;
    }
    
    public boolean isConfirmed() {
        return status == BookingStatus.CONFIRMED;
    }
    
    public boolean isCompleted() {
        return status == BookingStatus.COMPLETED;
    }
    
    public enum BookingStatus {
        PENDING, CONFIRMED, COMPLETED, CANCELED
    }
}
