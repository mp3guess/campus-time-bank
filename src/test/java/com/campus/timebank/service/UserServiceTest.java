package com.campus.timebank.service;

import com.campus.timebank.dto.UserDto;
import com.campus.timebank.entity.User;
import com.campus.timebank.mapper.UserMapper;
import com.campus.timebank.repository.UserRepository;
import com.campus.timebank.security.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private UserMapper userMapper;
    
    @Mock
    private SecurityContext securityContext;
    
    @Mock
    private Authentication authentication;
    
    @InjectMocks
    private UserService userService;
    
    private User testUser;
    private UserDto testUserDto;
    private UserPrincipal testUserPrincipal;
    
    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .faculty("Engineering")
                .role(User.UserRole.STUDENT)
                .active(true)
                .build();
        
        testUserDto = UserDto.builder()
                .id(1L)
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .faculty("Engineering")
                .role(User.UserRole.STUDENT)
                .active(true)
                .build();
        
        testUserPrincipal = new UserPrincipal(1L, "test@example.com", "password", null);
    }
    
    @Test
    void getCurrentUser_ShouldReturnUserDto_WhenUserExists() {
        // Arrange
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(testUserPrincipal);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userMapper.toDto(testUser)).thenReturn(testUserDto);
        
        // Act
        UserDto result = userService.getCurrentUser();
        
        // Assert
        assertNotNull(result);
        assertEquals(testUserDto.getId(), result.getId());
        assertEquals(testUserDto.getEmail(), result.getEmail());
        verify(userRepository, times(1)).findById(1L);
    }
    
    @Test
    void getUserById_ShouldReturnUserDto_WhenUserExists() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userMapper.toDto(testUser)).thenReturn(testUserDto);
        
        // Act
        UserDto result = userService.getUserById(1L);
        
        // Assert
        assertNotNull(result);
        assertEquals(testUserDto.getId(), result.getId());
        assertEquals(testUserDto.getEmail(), result.getEmail());
        verify(userRepository, times(1)).findById(1L);
    }
    
    @Test
    void getUserById_ShouldThrowException_WhenUserNotFound() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userService.getUserById(999L));
        verify(userRepository, times(1)).findById(999L);
    }
    
    @Test
    void getUserByEmail_ShouldReturnUserDto_WhenUserExists() {
        // Arrange
        String email = "test@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));
        when(userMapper.toDto(testUser)).thenReturn(testUserDto);
        
        // Act
        UserDto result = userService.getUserByEmail(email);
        
        // Assert
        assertNotNull(result);
        assertEquals(email, result.getEmail());
        verify(userRepository, times(1)).findByEmail(email);
    }
}
