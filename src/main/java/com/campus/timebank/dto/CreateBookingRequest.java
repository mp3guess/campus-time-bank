package com.campus.timebank.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateBookingRequest {
    
    @NotNull(message = "Offer ID is required")
    private Long offerId;
    
    @Positive(message = "Hours must be positive")
    private BigDecimal hours;
}
