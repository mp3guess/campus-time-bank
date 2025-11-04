package com.campus.timebank.controller;

import com.campus.timebank.dto.AuthResponse;
import com.campus.timebank.dto.CreateAdminRequest;
import com.campus.timebank.dto.TransactionDto;
import com.campus.timebank.dto.UpdateUserRoleRequest;
import com.campus.timebank.dto.UserDto;
import com.campus.timebank.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    
    private final AdminService adminService;
    
    // Admin Creation Endpoint (for creating first admin without auth)
    
    @PostMapping("/create-admin")
    public ResponseEntity<AuthResponse> createAdmin(@Valid @RequestBody CreateAdminRequest request) {
        AuthResponse response = adminService.createAdmin(request);
        return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED).body(response);
    }
    
    // User Management Endpoints
    
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserDto>> getAllUsers(Pageable pageable) {
        Page<UserDto> users = adminService.getAllUsers(pageable);
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/users/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long userId) {
        UserDto user = adminService.getUserById(userId);
        return ResponseEntity.ok(user);
    }
    
    @PutMapping("/users/{userId}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> activateUser(@PathVariable Long userId) {
        UserDto user = adminService.activateUser(userId);
        return ResponseEntity.ok(user);
    }
    
    @PutMapping("/users/{userId}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> deactivateUser(@PathVariable Long userId) {
        UserDto user = adminService.deactivateUser(userId);
        return ResponseEntity.ok(user);
    }
    
    @PutMapping("/users/{userId}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> updateUserRole(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateUserRoleRequest request) {
        UserDto user = adminService.updateUserRole(userId, request);
        return ResponseEntity.ok(user);
    }
    
    // Transaction Management Endpoints
    
    @GetMapping("/transactions")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<TransactionDto>> getAllTransactions(Pageable pageable) {
        Page<TransactionDto> transactions = adminService.getAllTransactions(pageable);
        return ResponseEntity.ok(transactions);
    }
    
    @GetMapping("/transactions/{transactionId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TransactionDto> getTransactionById(@PathVariable Long transactionId) {
        TransactionDto transaction = adminService.getTransactionById(transactionId);
        return ResponseEntity.ok(transaction);
    }
    
    @GetMapping("/transactions/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<TransactionDto>> getTransactionsByUserId(
            @PathVariable Long userId,
            Pageable pageable) {
        Page<TransactionDto> transactions = adminService.getTransactionsByUserId(userId, pageable);
        return ResponseEntity.ok(transactions);
    }
    
    @GetMapping("/transactions/type/{type}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<TransactionDto>> getTransactionsByType(
            @PathVariable String type,
            Pageable pageable) {
        Page<TransactionDto> transactions = adminService.getTransactionsByType(type, pageable);
        return ResponseEntity.ok(transactions);
    }
    
    @GetMapping("/transactions/date-range")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<TransactionDto>> getTransactionsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            Pageable pageable) {
        Page<TransactionDto> transactions = adminService.getTransactionsByDateRange(
                startDate, endDate, pageable);
        return ResponseEntity.ok(transactions);
    }
}

