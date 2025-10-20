package com.campus.timebank.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingDto {
    private Long id;
    private Long offerId;
    private String offerTitle;
    private Long requesterId;
    private String requesterName;
    private Long ownerId;
    private String ownerName;
    private String status;
    private BigDecimal reservedHours;
    private BigDecimal transferredHours;
    private String cancelReason;
    private LocalDateTime createdAt;
    private LocalDateTime confirmedAt;
    private LocalDateTime completedAt;
    private LocalDateTime canceledAt;
    private LocalDateTime updatedAt;
}
