package com.campus.timebank.service;

import com.campus.timebank.dto.AuthResponse;
import com.campus.timebank.dto.LoginRequest;
import com.campus.timebank.dto.RegisterRequest;
import com.campus.timebank.entity.User;
import com.campus.timebank.entity.Wallet;
import com.campus.timebank.mapper.UserMapper;
import com.campus.timebank.repository.UserRepository;
import com.campus.timebank.repository.WalletRepository;
import com.campus.timebank.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private WalletRepository walletRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @Mock
    private JwtTokenProvider tokenProvider;
    
    @Mock
    private AuthenticationManager authenticationManager;
    
    @Mock
    private UserMapper userMapper;
    
    @InjectMocks
    private AuthService authService;
    
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User testUser;
    
    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authService, "initialBalance", new BigDecimal("10.00"));
        
        registerRequest = RegisterRequest.builder()
                .email("newuser@example.com")
                .password("password123")
                .firstName("Jane")
                .lastName("Smith")
                .faculty("Computer Science")
                .studentId("ST12345")
                .build();
        
        loginRequest = LoginRequest.builder()
                .email("test@example.com")
                .password("password123")
                .build();
        
        testUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .password("encodedPassword")
                .firstName("John")
                .lastName("Doe")
                .faculty("Engineering")
                .role(User.UserRole.STUDENT)
                .active(true)
                .build();
    }
    
    @Test
    void register_ShouldCreateUserAndWallet_WhenEmailIsUnique() {
        // Arrange
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(walletRepository.save(any(Wallet.class))).thenReturn(new Wallet());
        when(tokenProvider.generateTokenFromEmail(anyString(), anyLong())).thenReturn("jwt-token");
        when(userMapper.toDto(any(User.class))).thenReturn(null);
        
        // Act
        AuthResponse response = authService.register(registerRequest);
        
        // Assert
        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        assertEquals("Bearer", response.getType());
        verify(userRepository, times(1)).save(any(User.class));
        verify(walletRepository, times(1)).save(any(Wallet.class));
    }
    
    @Test
    void register_ShouldThrowException_WhenEmailAlreadyExists() {
        // Arrange
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);
        
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> authService.register(registerRequest));
        verify(userRepository, never()).save(any(User.class));
        verify(walletRepository, never()).save(any(Wallet.class));
    }
    
    @Test
    void login_ShouldReturnAuthResponse_WhenCredentialsAreValid() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(tokenProvider.generateToken(authentication)).thenReturn("jwt-token");
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(testUser));
        when(userMapper.toDto(testUser)).thenReturn(null);
        
        // Act
        AuthResponse response = authService.login(loginRequest);
        
        // Assert
        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        verify(authenticationManager, times(1)).authenticate(any());
    }
}

