package com.campus.timebank.service;

import com.campus.timebank.dto.AuthResponse;
import com.campus.timebank.dto.CreateAdminRequest;
import com.campus.timebank.dto.TransactionDto;
import com.campus.timebank.dto.UpdateUserRoleRequest;
import com.campus.timebank.dto.UserDto;
import com.campus.timebank.entity.Transaction;
import com.campus.timebank.entity.User;
import com.campus.timebank.entity.Wallet;
import com.campus.timebank.mapper.UserMapper;
import com.campus.timebank.repository.TransactionRepository;
import com.campus.timebank.repository.UserRepository;
import com.campus.timebank.repository.WalletRepository;
import com.campus.timebank.security.JwtTokenProvider;
import com.campus.timebank.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AdminService {
    
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    
    @Value("${app.wallet.initial-balance:10.00}")
    private BigDecimal initialBalance;
    
    @Transactional
    public AuthResponse createAdmin(CreateAdminRequest request) {
        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }

        User adminUser = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .faculty(request.getFaculty())
                .role(User.UserRole.ADMIN)
                .active(true)
                .build();
        
        User savedAdmin = userRepository.save(adminUser);

        Wallet wallet = Wallet.builder()
                .user(savedAdmin)
                .balance(initialBalance)
                .totalEarned(initialBalance)
                .totalSpent(BigDecimal.ZERO)
                .build();
        
        walletRepository.save(wallet);

        String token = tokenProvider.generateTokenFromEmail(savedAdmin.getEmail(), savedAdmin.getId());

        UserDto userDto = userMapper.toDto(savedAdmin);
        
        return new AuthResponse(token, userDto);
    }
    
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN')")
    public Page<UserDto> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(userMapper::toDto);
    }
    
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN')")
    public UserDto getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        return userMapper.toDto(user);
    }
    
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public UserDto activateUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        user.setActive(true);
        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }
    
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public UserDto deactivateUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        
        // Prevent deactivating admin users
        if (user.getRole() == User.UserRole.ADMIN) {
            throw new IllegalArgumentException("Cannot deactivate admin user");
        }
        
        user.setActive(false);
        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }
    
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public UserDto updateUserRole(Long userId, UpdateUserRoleRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        UserPrincipal currentAdmin = (UserPrincipal) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        Long currentAdminId = currentAdmin.getId();

        if (userId.equals(currentAdminId) && request.getRole() != User.UserRole.ADMIN) {
            throw new IllegalArgumentException("Cannot demote yourself from admin role");
        }

        if (user.getRole() == User.UserRole.ADMIN && request.getRole() != User.UserRole.ADMIN) {
            long adminCount = userRepository.findAll().stream()
                    .filter(u -> u.getRole() == User.UserRole.ADMIN && u.getId() != userId)
                    .count();
            if (adminCount == 0) {
                throw new IllegalArgumentException("Cannot demote the last admin user");
            }
        }
        
        user.setRole(request.getRole());
        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }
    
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN')")
    public Page<TransactionDto> getAllTransactions(Pageable pageable) {
        return transactionRepository.findAll(pageable)
                .map(this::toTransactionDto);
    }
    
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN')")
    public Page<TransactionDto> getTransactionsByUserId(Long userId, Pageable pageable) {
        return transactionRepository.findByUserId(userId, pageable)
                .map(this::toTransactionDto);
    }
    
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN')")
    public Page<TransactionDto> getTransactionsByType(String type, Pageable pageable) {
        try {
            Transaction.TransactionType transactionType = Transaction.TransactionType.valueOf(type.toUpperCase());
            return transactionRepository.findByType(transactionType, pageable)
                    .map(this::toTransactionDto);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid transaction type: " + type);
        }
    }
    
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN')")
    public Page<TransactionDto> getTransactionsByDateRange(
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable) {
        return transactionRepository.findByDateRange(startDate, endDate, pageable)
                .map(this::toTransactionDto);
    }
    
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN')")
    public TransactionDto getTransactionById(Long transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found with id: " + transactionId));
        return toTransactionDto(transaction);
    }
    
    private TransactionDto toTransactionDto(Transaction transaction) {
        User user = transaction.getUser();
        return TransactionDto.builder()
                .id(transaction.getId())
                .userId(user != null ? user.getId() : null)
                .userEmail(user != null ? user.getEmail() : null)
                .userName(user != null ? (user.getFirstName() + " " + user.getLastName()) : null)
                .type(transaction.getType().name())
                .amount(transaction.getAmount())
                .bookingId(transaction.getBooking() != null ? transaction.getBooking().getId() : null)
                .description(transaction.getDescription())
                .status(transaction.getStatus())
                .createdAt(transaction.getCreatedAt())
                .build();
    }
}

