package com.campus.timebank.dto;

import com.campus.timebank.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String faculty;
    private String studentId;
    private User.UserRole role;
    private Boolean active;
    private WalletDto wallet;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
