package com.campus.timebank.service;

import com.campus.timebank.dto.BookingDto;
import com.campus.timebank.dto.CreateBookingRequest;
import com.campus.timebank.entity.*;
import com.campus.timebank.mapper.BookingMapper;
import com.campus.timebank.repository.*;
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
class BookingServiceTest {
    
    @Mock
    private BookingRepository bookingRepository;
    
    @Mock
    private OfferRepository offerRepository;
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private WalletRepository walletRepository;
    
    @Mock
    private TransactionRepository transactionRepository;
    
    @Mock
    private BookingMapper bookingMapper;
    
    @Mock
    private SecurityContext securityContext;
    
    @Mock
    private Authentication authentication;
    
    @InjectMocks
    private BookingService bookingService;
    
    private User requester;
    private User owner;
    private Offer offer;
    private Booking booking;
    private BookingDto bookingDto;
    private CreateBookingRequest createBookingRequest;
    private Wallet requesterWallet;
    private Wallet ownerWallet;
    private UserPrincipal requesterPrincipal;
    
    @BeforeEach
    void setUp() {
        requester = User.builder()
                .id(1L)
                .email("requester@example.com")
                .firstName("Jane")
                .lastName("Smith")
                .role(User.UserRole.STUDENT)
                .active(true)
                .build();
        
        owner = User.builder()
                .id(2L)
                .email("owner@example.com")
                .firstName("John")
                .lastName("Doe")
                .role(User.UserRole.STUDENT)
                .active(true)
                .build();
        
        offer = Offer.builder()
                .id(1L)
                .owner(owner)
                .title("Programming Tutoring")
                .description("Learn Java")
                .hoursRate(new BigDecimal("5.00"))
                .status(Offer.OfferStatus.ACTIVE)
                .available(true)
                .build();
        
        booking = Booking.builder()
                .id(1L)
                .offer(offer)
                .requester(requester)
                .status(Booking.BookingStatus.PENDING)
                .reservedHours(new BigDecimal("2.00"))
                .build();
        
        bookingDto = BookingDto.builder()
                .id(1L)
                .offerId(1L)
                .offerTitle("Programming Tutoring")
                .requesterId(1L)
                .requesterName("Jane Smith")
                .ownerId(2L)
                .ownerName("John Doe")
                .status("PENDING")
                .reservedHours(new BigDecimal("2.00"))
                .build();
        
        createBookingRequest = CreateBookingRequest.builder()
                .offerId(1L)
                .hours(new BigDecimal("2.00"))
                .build();
        
        requesterWallet = Wallet.builder()
                .id(1L)
                .user(requester)
                .balance(new BigDecimal("10.00"))
                .totalEarned(BigDecimal.ZERO)
                .totalSpent(BigDecimal.ZERO)
                .build();
        
        ownerWallet = Wallet.builder()
                .id(2L)
                .user(owner)
                .balance(new BigDecimal("5.00"))
                .totalEarned(BigDecimal.ZERO)
                .totalSpent(BigDecimal.ZERO)
                .build();
        
        requesterPrincipal = new UserPrincipal(1L, "requester@example.com", "password", null);
    }
    
    @Test
    void createBooking_ShouldCreateAndReturnBookingDto_WhenValid() {
        // Arrange
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(requesterPrincipal);
        when(userRepository.findById(1L)).thenReturn(Optional.of(requester));
        when(offerRepository.findById(1L)).thenReturn(Optional.of(offer));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(bookingMapper.toDto(booking)).thenReturn(bookingDto);
        
        // Act
        BookingDto result = bookingService.createBooking(createBookingRequest);
        
        // Assert
        assertNotNull(result);
        assertEquals(bookingDto.getStatus(), result.getStatus());
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }
    
    @Test
    void createBooking_ShouldThrowException_WhenOfferInactive() {
        // Arrange
        offer.setStatus(Offer.OfferStatus.INACTIVE);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(requesterPrincipal);
        when(userRepository.findById(1L)).thenReturn(Optional.of(requester));
        when(offerRepository.findById(1L)).thenReturn(Optional.of(offer));
        
        // Act & Assert
        assertThrows(IllegalStateException.class, () -> bookingService.createBooking(createBookingRequest));
    }
    
    @Test
    void createBooking_ShouldThrowException_WhenUserIsOwner() {
        // Arrange
        requester.setId(2L); // Same as owner
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(requesterPrincipal);
        when(userRepository.findById(1L)).thenReturn(Optional.of(requester));
        when(offerRepository.findById(1L)).thenReturn(Optional.of(offer));
        
        // Act & Assert
        assertThrows(IllegalStateException.class, () -> bookingService.createBooking(createBookingRequest));
    }
    
    @Test
    void confirmBooking_ShouldConfirmAndReturnBookingDto_WhenValid() {
        // Arrange
        UserPrincipal ownerPrincipal = new UserPrincipal(2L, "owner@example.com", "password", null);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(ownerPrincipal);
        
        when(bookingRepository.findByIdAndOfferOwnerId(1L, 2L)).thenReturn(Optional.of(booking));
        when(walletRepository.findByUserId(1L)).thenReturn(Optional.of(requesterWallet));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(bookingMapper.toDto(booking)).thenReturn(bookingDto);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(new Transaction());
        
        // Act
        BookingDto result = bookingService.confirmBooking(1L);
        
        // Assert
        assertNotNull(result);
        verify(bookingRepository, times(1)).findByIdAndOfferOwnerId(1L, 2L);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }
    
    @Test
    void confirmBooking_ShouldThrowException_WhenInsufficientBalance() {
        // Arrange
        UserPrincipal ownerPrincipal = new UserPrincipal(2L, "owner@example.com", "password", null);
        requesterWallet.setBalance(new BigDecimal("1.00")); // Insufficient
        
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(ownerPrincipal);
        
        when(bookingRepository.findByIdAndOfferOwnerId(1L, 2L)).thenReturn(Optional.of(booking));
        when(walletRepository.findByUserId(1L)).thenReturn(Optional.of(requesterWallet));
        
        // Act & Assert
        assertThrows(IllegalStateException.class, () -> bookingService.confirmBooking(1L));
    }
    
    @Test
    void completeBooking_ShouldCompleteAndTransferHours_WhenConfirmed() {
        // Arrange
        booking.setStatus(Booking.BookingStatus.CONFIRMED);
        UserPrincipal ownerPrincipal = new UserPrincipal(2L, "owner@example.com", "password", null);
        
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(ownerPrincipal);
        
        when(bookingRepository.findByIdAndOfferOwnerId(1L, 2L)).thenReturn(Optional.of(booking));
        when(walletRepository.findByUserId(1L)).thenReturn(Optional.of(requesterWallet));
        when(walletRepository.findByUserId(2L)).thenReturn(Optional.of(ownerWallet));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(bookingMapper.toDto(booking)).thenReturn(bookingDto);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(new Transaction());
        when(walletRepository.save(any(Wallet.class))).thenReturn(requesterWallet);
        
        // Act
        BookingDto result = bookingService.completeBooking(1L);
        
        // Assert
        assertNotNull(result);
        verify(walletRepository, times(2)).save(any(Wallet.class));
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }
    
    @Test
    void completeBooking_ShouldThrowException_WhenNotConfirmed() {
        // Arrange
        booking.setStatus(Booking.BookingStatus.PENDING);
        UserPrincipal ownerPrincipal = new UserPrincipal(2L, "owner@example.com", "password", null);
        
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(ownerPrincipal);
        
        when(bookingRepository.findByIdAndOfferOwnerId(1L, 2L)).thenReturn(Optional.of(booking));
        
        // Act & Assert
        assertThrows(IllegalStateException.class, () -> bookingService.completeBooking(1L));
    }
    
    @Test
    void cancelBooking_ShouldCancelAndReturnBookingDto_WhenValid() {
        // Arrange
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(requesterPrincipal);
        
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(bookingMapper.toDto(booking)).thenReturn(bookingDto);
        
        // Act
        BookingDto result = bookingService.cancelBooking(1L, "Changed my mind");
        
        // Assert
        assertNotNull(result);
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }
    
    @Test
    void getBookingById_ShouldReturnBookingDto_WhenBookingExists() {
        // Arrange
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingMapper.toDto(booking)).thenReturn(bookingDto);
        
        // Act
        BookingDto result = bookingService.getBookingById(1L);
        
        // Assert
        assertNotNull(result);
        assertEquals(bookingDto.getId(), result.getId());
        verify(bookingRepository, times(1)).findById(1L);
    }
    
    @Test
    void getMyBookingsAsRequester_ShouldReturnRequesterBookings() {
        // Arrange
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(requesterPrincipal);
        
        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking);
        Page<Booking> bookingsPage = new PageImpl<>(bookings);
        
        when(bookingRepository.findByRequesterId(1L, Pageable.unpaged())).thenReturn(bookingsPage);
        when(bookingMapper.toDto(any(Booking.class))).thenReturn(bookingDto);
        
        // Act
        Page<BookingDto> result = bookingService.getMyBookingsAsRequester(Pageable.unpaged());
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(bookingRepository, times(1)).findByRequesterId(1L, Pageable.unpaged());
    }
}
