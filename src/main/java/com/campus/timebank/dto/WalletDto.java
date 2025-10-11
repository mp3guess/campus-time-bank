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
public class WalletDto {
    private Long id;
    private Long userId;
    private BigDecimal balance;
    private BigDecimal totalEarned;
    private BigDecimal totalSpent;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
