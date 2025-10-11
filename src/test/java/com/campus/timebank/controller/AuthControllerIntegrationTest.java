package com.campus.timebank.controller;

import com.campus.timebank.dto.LoginRequest;
import com.campus.timebank.dto.RegisterRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class AuthControllerIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    void register_ShouldReturnCreated_WhenValidRequest() throws Exception {
        // Arrange
        RegisterRequest request = RegisterRequest.builder()
                .email("integration@test.com")
                .password("password123")
                .firstName("Integration")
                .lastName("Test")
                .faculty("Computer Science")
                .studentId("IT001")
                .build();
        
        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.type").value("Bearer"))
                .andExpect(jsonPath("$.user").exists());
    }
    
    @Test
    void register_ShouldReturnBadRequest_WhenEmailAlreadyExists() throws Exception {
        // Arrange - First registration
        RegisterRequest request1 = RegisterRequest.builder()
                .email("duplicate@test.com")
                .password("password123")
                .firstName("First")
                .lastName("User")
                .faculty("Engineering")
                .build();
        
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request1)));
        
        // Second registration with same email
        RegisterRequest request2 = RegisterRequest.builder()
                .email("duplicate@test.com")
                .password("different123")
                .firstName("Second")
                .lastName("User")
                .faculty("Science")
                .build();
        
        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().is4xxClientError());
    }
    
    @Test
    void register_ShouldReturnBadRequest_WhenInvalidEmail() throws Exception {
        // Arrange
        RegisterRequest request = RegisterRequest.builder()
                .email("invalid-email")
                .password("password123")
                .firstName("Test")
                .lastName("User")
                .build();
        
        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}

