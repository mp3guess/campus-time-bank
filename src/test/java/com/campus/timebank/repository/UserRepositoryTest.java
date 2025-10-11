package com.campus.timebank.repository;

import com.campus.timebank.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Autowired
    private UserRepository userRepository;
    
    @Test
    void findByEmail_ShouldReturnUser_WhenEmailExists() {
        // Arrange
        User user = User.builder()
                .email("test@example.com")
                .password("password")
                .firstName("John")
                .lastName("Doe")
                .faculty("Engineering")
                .role(User.UserRole.STUDENT)
                .active(true)
                .build();
        entityManager.persist(user);
        entityManager.flush();
        
        // Act
        Optional<User> found = userRepository.findByEmail("test@example.com");
        
        // Assert
        assertTrue(found.isPresent());
        assertEquals("test@example.com", found.get().getEmail());
    }
    
    @Test
    void existsByEmail_ShouldReturnTrue_WhenEmailExists() {
        // Arrange
        User user = User.builder()
                .email("exists@example.com")
                .password("password")
                .firstName("Jane")
                .lastName("Smith")
                .faculty("Science")
                .role(User.UserRole.STUDENT)
                .active(true)
                .build();
        entityManager.persist(user);
        entityManager.flush();
        
        // Act
        boolean exists = userRepository.existsByEmail("exists@example.com");
        
        // Assert
        assertTrue(exists);
    }
    
    @Test
    void existsByEmail_ShouldReturnFalse_WhenEmailDoesNotExist() {
        // Act
        boolean exists = userRepository.existsByEmail("nonexistent@example.com");
        
        // Assert
        assertFalse(exists);
    }
    
    @Test
    void findByEmailAndActiveTrue_ShouldReturnUser_WhenUserIsActive() {
        // Arrange
        User user = User.builder()
                .email("active@example.com")
                .password("password")
                .firstName("Active")
                .lastName("User")
                .faculty("Engineering")
                .role(User.UserRole.STUDENT)
                .active(true)
                .build();
        entityManager.persist(user);
        entityManager.flush();
        
        // Act
        Optional<User> found = userRepository.findByEmailAndActiveTrue("active@example.com");
        
        // Assert
        assertTrue(found.isPresent());
        assertTrue(found.get().getActive());
    }
}

