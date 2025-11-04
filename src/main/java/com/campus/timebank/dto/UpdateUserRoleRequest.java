package com.campus.timebank.dto;

import com.campus.timebank.entity.User;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUserRoleRequest {
    
    @NotNull(message = "Role is required")
    private User.UserRole role;
}

