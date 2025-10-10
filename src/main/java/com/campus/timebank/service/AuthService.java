package com.campus.timebank.service;

import com.campus.timebank.dto.AuthResponse;
import com.campus.timebank.dto.LoginRequest;
import com.campus.timebank.dto.RegisterRequest;
import com.campus.timebank.dto.UserDto;
import com.campus.timebank.entity.User;
import com.campus.timebank.entity.Wallet;
import com.campus.timebank.mapper.UserMapper;
import com.campus.timebank.repository.UserRepository;
import com.campus.timebank.repository.WalletRepository;
import com.campus.timebank.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;
    
    @Value("${app.wallet.initial-balance:10.00}")
    private BigDecimal initialBalance;
    
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }
        
        // Create user
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .faculty(request.getFaculty())
                .studentId(request.getStudentId())
                .role(User.UserRole.STUDENT)
                .active(true)
                .build();
        
        User savedUser = userRepository.save(user);
        
        // Create wallet with initial balance
        Wallet wallet = Wallet.builder()
                .user(savedUser)
                .balance(initialBalance)
                .totalEarned(initialBalance)
                .totalSpent(BigDecimal.ZERO)
                .build();
        
        walletRepository.save(wallet);
        
        // Generate JWT token
        String token = tokenProvider.generateTokenFromEmail(savedUser.getEmail(), savedUser.getId());
        
        // Convert to DTO
        UserDto userDto = userMapper.toDto(savedUser);
        
        return new AuthResponse(token, userDto);
    }
    
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        String token = tokenProvider.generateToken(authentication);
        
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        UserDto userDto = userMapper.toDto(user);
        
        return new AuthResponse(token, userDto);
    }
}

