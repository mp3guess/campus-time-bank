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
public class TransactionDto {
    private Long id;
    private Long userId;
    private String userEmail;
    private String userName;
    private String type;
    private BigDecimal amount;
    private Long bookingId;
    private String description;
    private String status;
    private LocalDateTime createdAt;
}

