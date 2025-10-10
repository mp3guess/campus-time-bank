package com.campus.timebank.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {
    private String token;
    private String type = "Bearer";
    private UserDto user;
    
    public AuthResponse(String token, UserDto user) {
        this.token = token;
        this.user = user;
        this.type = "Bearer";
    }
}

