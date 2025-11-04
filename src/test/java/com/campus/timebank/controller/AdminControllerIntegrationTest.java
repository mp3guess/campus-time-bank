package com.campus.timebank.controller;

import com.campus.timebank.dto.CreateAdminRequest;
import com.campus.timebank.dto.UpdateUserRoleRequest;
import com.campus.timebank.entity.User;
import com.campus.timebank.repository.UserRepository;
import com.campus.timebank.security.JwtTokenProvider;
import com.campus.timebank.security.UserPrincipal;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class AdminControllerIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private JwtTokenProvider tokenProvider;
    
    private User adminUser;
    private User studentUser;
    private String adminToken;
    
    @BeforeEach
    void setUp() {
        // Create admin user
        adminUser = User.builder()
                .email("admin@test.com")
                .password("$2a$10$encodedPassword")
                .firstName("Admin")
                .lastName("User")
                .role(User.UserRole.ADMIN)
                .active(true)
                .build();
        adminUser = userRepository.save(adminUser);
        
        // Create student user
        studentUser = User.builder()
                .email("student@test.com")
                .password("$2a$10$encodedPassword")
                .firstName("Student")
                .lastName("User")
                .role(User.UserRole.STUDENT)
                .active(true)
                .build();
        studentUser = userRepository.save(studentUser);
        
        // Generate admin token
        adminToken = tokenProvider.generateTokenFromEmail(adminUser.getEmail(), adminUser.getId());
    }
    
    @Test
    void createAdmin_ShouldCreateAdmin_WhenNoAuth() throws Exception {
        // Arrange
        CreateAdminRequest request = CreateAdminRequest.builder()
                .email("newadmin@test.com")
                .password("password123")
                .firstName("New")
                .lastName("Admin")
                .faculty("Administration")
                .build();
        
        // Act & Assert
        mockMvc.perform(post("/api/admin/create-admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.user.role").value("ADMIN"));
    }
    
    @Test
    void getAllUsers_ShouldReturnForbidden_WhenNotAdmin() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/admin/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
    
    @Test
    void getAllUsers_ShouldReturnUsers_WhenAdmin() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/admin/users?page=0&size=10")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }
    
    @Test
    void getUserById_ShouldReturnUser_WhenAdmin() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/admin/users/" + studentUser.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(studentUser.getId()));
    }
    
    @Test
    void activateUser_ShouldActivateUser_WhenAdmin() throws Exception {
        // Arrange
        studentUser.setActive(false);
        userRepository.save(studentUser);
        
        // Act & Assert
        mockMvc.perform(put("/api/admin/users/" + studentUser.getId() + "/activate")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(true));
    }
    
    @Test
    void deactivateUser_ShouldDeactivateUser_WhenAdmin() throws Exception {
        // Act & Assert
        mockMvc.perform(put("/api/admin/users/" + studentUser.getId() + "/deactivate")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false));
    }
    
    @Test
    void updateUserRole_ShouldUpdateRole_WhenAdmin() throws Exception {
        // Arrange
        UpdateUserRoleRequest request = UpdateUserRoleRequest.builder()
                .role(User.UserRole.ADMIN)
                .build();
        
        // Act & Assert
        mockMvc.perform(put("/api/admin/users/" + studentUser.getId() + "/role")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }
    
    @Test
    void getAllTransactions_ShouldReturnTransactions_WhenAdmin() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/admin/transactions?page=0&size=10")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }
}

