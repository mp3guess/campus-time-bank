package com.campus.timebank.service;

import com.campus.timebank.dto.CreateOfferRequest;
import com.campus.timebank.dto.OfferDto;
import com.campus.timebank.entity.Offer;
import com.campus.timebank.entity.User;
import com.campus.timebank.mapper.OfferMapper;
import com.campus.timebank.repository.OfferRepository;
import com.campus.timebank.repository.UserRepository;
import com.campus.timebank.security.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OfferServiceTest {
    
    @Mock
    private OfferRepository offerRepository;
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private OfferMapper offerMapper;
    
    @Mock
    private SecurityContext securityContext;
    
    @Mock
    private Authentication authentication;
    
    @InjectMocks
    private OfferService offerService;
    
    private User testOwner;
    private Offer testOffer;
    private OfferDto testOfferDto;
    private CreateOfferRequest createOfferRequest;
    private UserPrincipal testUserPrincipal;
    
    @BeforeEach
    void setUp() {
        testOwner = User.builder()
                .id(1L)
                .email("owner@example.com")
                .firstName("John")
                .lastName("Doe")
                .role(User.UserRole.STUDENT)
                .active(true)
                .build();
        
        testOffer = Offer.builder()
                .id(1L)
                .owner(testOwner)
                .title("Programming Tutoring")
                .description("Learn Java programming")
                .hoursRate(new BigDecimal("5.00"))
                .status(Offer.OfferStatus.ACTIVE)
                .available(true)
                .build();
        
        testOfferDto = OfferDto.builder()
                .id(1L)
                .ownerId(1L)
                .ownerName("John Doe")
                .title("Programming Tutoring")
                .description("Learn Java programming")
                .hoursRate(new BigDecimal("5.00"))
                .status("ACTIVE")
                .available(true)
                .bookingCount(0)
                .build();
        
        createOfferRequest = CreateOfferRequest.builder()
                .title("Programming Tutoring")
                .description("Learn Java programming")
                .hoursRate(new BigDecimal("5.00"))
                .build();
        
        testUserPrincipal = new UserPrincipal(1L, "owner@example.com", "password", null);
    }
    
    @Test
    void createOffer_ShouldCreateAndReturnOfferDto_WhenValid() {
        // Arrange
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(testUserPrincipal);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testOwner));
        when(offerMapper.toEntity(createOfferRequest)).thenReturn(testOffer);
        when(offerRepository.save(any(Offer.class))).thenReturn(testOffer);
        when(offerMapper.toDto(testOffer)).thenReturn(testOfferDto);
        
        // Act
        OfferDto result = offerService.createOffer(createOfferRequest);
        
        // Assert
        assertNotNull(result);
        assertEquals(testOfferDto.getTitle(), result.getTitle());
        assertEquals(testOfferDto.getOwnerId(), result.getOwnerId());
        verify(offerRepository, times(1)).save(any(Offer.class));
    }
    
    @Test
    void getOfferById_ShouldReturnOfferDto_WhenOfferExists() {
        // Arrange
        when(offerRepository.findById(1L)).thenReturn(Optional.of(testOffer));
        when(offerMapper.toDto(testOffer)).thenReturn(testOfferDto);
        
        // Act
        OfferDto result = offerService.getOfferById(1L);
        
        // Assert
        assertNotNull(result);
        assertEquals(testOfferDto.getId(), result.getId());
        assertEquals(testOfferDto.getTitle(), result.getTitle());
        verify(offerRepository, times(1)).findById(1L);
    }
    
    @Test
    void getOfferById_ShouldThrowException_WhenOfferNotFound() {
        // Arrange
        when(offerRepository.findById(999L)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> offerService.getOfferById(999L));
        verify(offerRepository, times(1)).findById(999L);
    }
    
    @Test
    void getActiveOffers_ShouldReturnActiveOffers() {
        // Arrange
        List<Offer> offers = new ArrayList<>();
        offers.add(testOffer);
        Page<Offer> offersPage = new PageImpl<>(offers);
        Page<OfferDto> expectedPage = new PageImpl<>(List.of(testOfferDto));
        
        when(offerRepository.findByStatusAndAvailableTrue("ACTIVE", Pageable.unpaged()))
                .thenReturn(offersPage);
        when(offerMapper.toDto(any(Offer.class))).thenReturn(testOfferDto);
        
        // Act
        Page<OfferDto> result = offerService.getActiveOffers(Pageable.unpaged());
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(offerRepository, times(1)).findByStatusAndAvailableTrue("ACTIVE", Pageable.unpaged());
    }
    
    @Test
    void getOffersByOwner_ShouldReturnOffersByOwner() {
        // Arrange
        List<Offer> offers = new ArrayList<>();
        offers.add(testOffer);
        Page<Offer> offersPage = new PageImpl<>(offers);
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(testOwner));
        when(offerRepository.findByOwner(testOwner, Pageable.unpaged())).thenReturn(offersPage);
        when(offerMapper.toDto(any(Offer.class))).thenReturn(testOfferDto);
        
        // Act
        Page<OfferDto> result = offerService.getOffersByOwner(1L, Pageable.unpaged());
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(userRepository, times(1)).findById(1L);
    }
    
    @Test
    void getMyOffers_ShouldReturnCurrentUserOffers() {
        // Arrange
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(testUserPrincipal);
        
        List<Offer> offers = new ArrayList<>();
        offers.add(testOffer);
        
        when(offerRepository.findByOwnerId(1L)).thenReturn(offers);
        when(offerMapper.toDto(any(Offer.class))).thenReturn(testOfferDto);
        
        // Act
        List<OfferDto> result = offerService.getMyOffers();
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(offerRepository, times(1)).findByOwnerId(1L);
    }
    
    @Test
    void updateOffer_ShouldUpdateAndReturnOffer_WhenAuthorized() {
        // Arrange
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(testUserPrincipal);
        
        CreateOfferRequest updateRequest = CreateOfferRequest.builder()
                .title("Updated Title")
                .description("Updated Description")
                .hoursRate(new BigDecimal("10.00"))
                .build();
        
        // Create an updated offer to return after save
        Offer updatedOffer = Offer.builder()
                .id(1L)
                .owner(testOwner)
                .title("Updated Title")
                .description("Updated Description")
                .hoursRate(new BigDecimal("10.00"))
                .status(Offer.OfferStatus.ACTIVE)
                .available(true)
                .build();
        
        when(offerRepository.findByIdAndOwnerId(1L, 1L)).thenReturn(Optional.of(testOffer));
        when(offerRepository.save(any(Offer.class))).thenReturn(updatedOffer);
        when(offerMapper.toDto(updatedOffer)).thenReturn(testOfferDto);
        
        // Act
        OfferDto result = offerService.updateOffer(1L, updateRequest);
        
        // Assert
        assertNotNull(result);
        verify(offerRepository, times(1)).findByIdAndOwnerId(1L, 1L);
        verify(offerRepository, times(1)).save(any(Offer.class));
    }
    
    @Test
    void deactivateOffer_ShouldDeactivateOffer_WhenAuthorized() {
        // Arrange
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(testUserPrincipal);
        
        when(offerRepository.findByIdAndOwnerId(1L, 1L)).thenReturn(Optional.of(testOffer));
        when(offerRepository.save(any(Offer.class))).thenReturn(testOffer);
        
        // Act
        offerService.deactivateOffer(1L);
        
        // Assert
        verify(offerRepository, times(1)).findByIdAndOwnerId(1L, 1L);
        verify(offerRepository, times(1)).save(any(Offer.class));
    }
    
    @Test
    void activateOffer_ShouldActivateOffer_WhenAuthorized() {
        // Arrange
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(testUserPrincipal);
        
        testOffer.setStatus(Offer.OfferStatus.INACTIVE);
        when(offerRepository.findByIdAndOwnerId(1L, 1L)).thenReturn(Optional.of(testOffer));
        when(offerRepository.save(any(Offer.class))).thenReturn(testOffer);
        
        // Act
        offerService.activateOffer(1L);
        
        // Assert
        verify(offerRepository, times(1)).findByIdAndOwnerId(1L, 1L);
        verify(offerRepository, times(1)).save(any(Offer.class));
    }
}
