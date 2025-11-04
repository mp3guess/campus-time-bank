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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private TransactionRepository transactionRepository;
    
    @Mock
    private WalletRepository walletRepository;
    
    @Mock
    private UserMapper userMapper;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @Mock
    private JwtTokenProvider tokenProvider;
    
    @InjectMocks
    private AdminService adminService;
    
    private User testUser;
    private User adminUser;
    private Transaction testTransaction;
    private CreateAdminRequest createAdminRequest;
    
    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(adminService, "initialBalance", new BigDecimal("10.00"));
        
        testUser = User.builder()
                .id(1L)
                .email("student@test.com")
                .password("encodedPassword")
                .firstName("John")
                .lastName("Doe")
                .faculty("Computer Science")
                .role(User.UserRole.STUDENT)
                .active(true)
                .build();
        
        adminUser = User.builder()
                .id(2L)
                .email("admin@test.com")
                .password("encodedPassword")
                .firstName("Admin")
                .lastName("User")
                .faculty("Administration")
                .role(User.UserRole.ADMIN)
                .active(true)
                .build();
        
        testTransaction = Transaction.builder()
                .id(1L)
                .user(testUser)
                .type(Transaction.TransactionType.RESERVE)
                .amount(new BigDecimal("5.00"))
                .status("COMPLETED")
                .createdAt(LocalDateTime.now())
                .build();
        
        createAdminRequest = CreateAdminRequest.builder()
                .email("newadmin@test.com")
                .password("password123")
                .firstName("New")
                .lastName("Admin")
                .faculty("Administration")
                .build();
    }
    
    @Test
    void createAdmin_ShouldCreateAdminUser_WhenValidRequest() {
        // Arrange
        when(userRepository.existsByEmail(createAdminRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(createAdminRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(adminUser);
        when(walletRepository.save(any(Wallet.class))).thenReturn(new Wallet());
        when(tokenProvider.generateTokenFromEmail(anyString(), anyLong())).thenReturn("jwt-token");
        when(userMapper.toDto(any(User.class))).thenReturn(UserDto.builder().build());
        
        // Act
        AuthResponse response = adminService.createAdmin(createAdminRequest);
        
        // Assert
        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        verify(userRepository, times(1)).save(any(User.class));
        verify(walletRepository, times(1)).save(any(Wallet.class));
    }
    
    @Test
    void createAdmin_ShouldThrowException_WhenEmailAlreadyExists() {
        // Arrange
        when(userRepository.existsByEmail(createAdminRequest.getEmail())).thenReturn(true);
        
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> adminService.createAdmin(createAdminRequest));
        verify(userRepository, never()).save(any(User.class));
    }
    
    @Test
    void getAllUsers_ShouldReturnPageOfUsers() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<User> users = Arrays.asList(testUser, adminUser);
        Page<User> userPage = new PageImpl<>(users, pageable, users.size());
        
        when(userRepository.findAll(pageable)).thenReturn(userPage);
        when(userMapper.toDto(any(User.class))).thenReturn(UserDto.builder().build());
        
        // Act
        Page<UserDto> result = adminService.getAllUsers(pageable);
        
        // Assert
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        verify(userRepository, times(1)).findAll(pageable);
    }
    
    @Test
    void activateUser_ShouldActivateUser_WhenUserExists() {
        // Arrange
        testUser.setActive(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userMapper.toDto(testUser)).thenReturn(UserDto.builder().active(true).build());
        
        // Act
        UserDto result = adminService.activateUser(1L);
        
        // Assert
        assertNotNull(result);
        assertTrue(result.getActive());
        assertTrue(testUser.getActive());
        verify(userRepository, times(1)).save(testUser);
    }
    
    @Test
    void deactivateUser_ShouldDeactivateUser_WhenUserExists() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userMapper.toDto(any(User.class))).thenReturn(UserDto.builder().active(false).build());
        
        // Act
        UserDto result = adminService.deactivateUser(1L);
        
        // Assert
        assertNotNull(result);
        assertFalse(result.getActive());
        assertFalse(testUser.getActive());
        verify(userRepository, times(1)).save(testUser);
    }
    
    @Test
    void deactivateUser_ShouldThrowException_WhenDeactivatingAdmin() {
        // Arrange
        when(userRepository.findById(2L)).thenReturn(Optional.of(adminUser));
        
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> adminService.deactivateUser(2L));
        verify(userRepository, never()).save(any(User.class));
    }
    
    @Test
    void updateUserRole_ShouldUpdateRole_WhenValidRequest() {
        // Arrange
        UpdateUserRoleRequest request = UpdateUserRoleRequest.builder()
                .role(User.UserRole.ADMIN)
                .build();
        
        UserPrincipal userPrincipal = new UserPrincipal(3L, "admin2@test.com", "password", null);
        SecurityContext securityContext = mock(SecurityContext.class);
        org.springframework.security.core.Authentication auth = mock(org.springframework.security.core.Authentication.class);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(auth);
        when(auth.getPrincipal()).thenReturn(userPrincipal);
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userMapper.toDto(testUser)).thenReturn(UserDto.builder().role(User.UserRole.ADMIN).build());
        
        // Act
        UserDto result = adminService.updateUserRole(1L, request);
        
        // Assert
        assertNotNull(result);
        assertEquals(User.UserRole.ADMIN, testUser.getRole());
        verify(userRepository, times(1)).save(testUser);
        // findAll is called only when demoting admin
        // Since we're promoting to admin, it might not be called
    }
    
    @Test
    void getAllTransactions_ShouldReturnPageOfTransactions() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<Transaction> transactions = Arrays.asList(testTransaction);
        Page<Transaction> transactionPage = new PageImpl<>(transactions, pageable, transactions.size());
        
        when(transactionRepository.findAll(pageable)).thenReturn(transactionPage);
        
        // Act
        Page<TransactionDto> result = adminService.getAllTransactions(pageable);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(transactionRepository, times(1)).findAll(pageable);
    }
    
    @Test
    void getTransactionById_ShouldReturnTransaction_WhenExists() {
        // Arrange
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(testTransaction));
        
        // Act
        TransactionDto result = adminService.getTransactionById(1L);
        
        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(transactionRepository, times(1)).findById(1L);
    }
    
    @Test
    void getTransactionById_ShouldThrowException_WhenNotFound() {
        // Arrange
        when(transactionRepository.findById(999L)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> adminService.getTransactionById(999L));
    }
}

