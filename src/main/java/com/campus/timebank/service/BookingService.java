package com.campus.timebank.service;

import com.campus.timebank.dto.BookingDto;
import com.campus.timebank.dto.CreateBookingRequest;
import com.campus.timebank.entity.Booking;
import com.campus.timebank.entity.Offer;
import com.campus.timebank.entity.Transaction;
import com.campus.timebank.entity.User;
import com.campus.timebank.entity.Wallet;
import com.campus.timebank.mapper.BookingMapper;
import com.campus.timebank.repository.BookingRepository;
import com.campus.timebank.repository.OfferRepository;
import com.campus.timebank.repository.TransactionRepository;
import com.campus.timebank.repository.UserRepository;
import com.campus.timebank.repository.WalletRepository;
import com.campus.timebank.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookingService {
    
    private final BookingRepository bookingRepository;
    private final OfferRepository offerRepository;
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final BookingMapper bookingMapper;
    
    @Transactional
    public BookingDto createBooking(CreateBookingRequest request) {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        
        User requester = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        Offer offer = offerRepository.findById(request.getOfferId())
                .orElseThrow(() -> new IllegalArgumentException("Offer not found"));
        
        if (!offer.isActive()) {
            throw new IllegalStateException("Offer is not available");
        }
        
        if (offer.getOwner().getId().equals(requester.getId())) {
            throw new IllegalStateException("Cannot book your own offer");
        }
        
        Booking booking = Booking.builder()
                .offer(offer)
                .requester(requester)
                .status(Booking.BookingStatus.PENDING)
                .reservedHours(request.getHours())
                .build();
        
        Booking savedBooking = bookingRepository.save(booking);
        return bookingMapper.toDto(savedBooking);
    }
    
    @Transactional
    public BookingDto confirmBooking(Long bookingId) {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        
        Booking booking = bookingRepository.findByIdAndOfferOwnerId(bookingId, userPrincipal.getId())
                .orElseThrow(() -> new IllegalArgumentException("Booking not found or you don't have permission"));
        
        // Get wallets for owner and requester
        Wallet ownerWallet = walletRepository.findByUserId(booking.getOffer().getOwner().getId())
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found for owner"));
        
        Wallet requesterWallet = walletRepository.findByUserId(booking.getRequester().getId())
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found for requester"));
        
        // Check if owner has sufficient balance (owner pays requester in this system)
        if (!ownerWallet.hasBalance(booking.getReservedHours())) {
            throw new IllegalStateException("Insufficient balance to confirm booking");
        }
        
        booking.confirm();
        
        // Transfer hours: owner pays, requester receives
        ownerWallet.deductBalance(booking.getReservedHours());
        requesterWallet.addBalance(booking.getReservedHours());
        
        walletRepository.save(ownerWallet);
        walletRepository.save(requesterWallet);
        
        // Create transaction for owner (deduction)
        Transaction ownerTransaction = Transaction.builder()
                .user(booking.getOffer().getOwner())
                .type(Transaction.TransactionType.RESERVE)
                .amount(booking.getReservedHours())
                .booking(booking)
                .description("Reserved hours for booking offer: " + booking.getOffer().getTitle())
                .build();
        transactionRepository.save(ownerTransaction);
        
        // Create transaction for requester (addition)
        Transaction requesterTransaction = Transaction.builder()
                .user(booking.getRequester())
                .type(Transaction.TransactionType.COMMIT)
                .amount(booking.getReservedHours())
                .booking(booking)
                .description("Received hours from booking offer: " + booking.getOffer().getTitle())
                .build();
        transactionRepository.save(requesterTransaction);
        
        Booking savedBooking = bookingRepository.save(booking);
        return bookingMapper.toDto(savedBooking);
    }
    
    @Transactional
    public BookingDto completeBooking(Long bookingId) {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        
        Booking booking = bookingRepository.findByIdAndOfferOwnerId(bookingId, userPrincipal.getId())
                .orElseThrow(() -> new IllegalArgumentException("Booking not found or you don't have permission"));
        
        if (booking.getStatus() != Booking.BookingStatus.CONFIRMED) {
            throw new IllegalStateException("Only confirmed bookings can be completed");
        }
        
        booking.complete();
        
        // Balance transfer already happened at confirmation, so we just mark as completed
        // Create transaction record for completion
        Transaction transaction = Transaction.builder()
                .user(booking.getOffer().getOwner())
                .type(Transaction.TransactionType.COMMIT)
                .amount(booking.getTransferredHours() != null ? booking.getTransferredHours() : booking.getReservedHours())
                .booking(booking)
                .description("Completed booking: " + booking.getOffer().getTitle())
                .build();
        transactionRepository.save(transaction);
        
        Booking savedBooking = bookingRepository.save(booking);
        return bookingMapper.toDto(savedBooking);
    }
    
    @Transactional
    public BookingDto cancelBooking(Long bookingId, String reason) {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));
        
        // Check if user is either requester or offer owner
        if (!booking.getRequester().getId().equals(userPrincipal.getId()) &&
            !booking.getOffer().getOwner().getId().equals(userPrincipal.getId())) {
            throw new IllegalStateException("You don't have permission to cancel this booking");
        }
        
        if (!booking.canBeCanceled()) {
            throw new IllegalStateException("This booking cannot be canceled");
        }
        
        booking.cancel(reason);
        
        // If booking was confirmed, release the reserved hours
        if (booking.getStatus() == Booking.BookingStatus.CONFIRMED) {
            Transaction transaction = Transaction.builder()
                    .user(booking.getRequester())
                    .type(Transaction.TransactionType.RELEASE)
                    .amount(booking.getReservedHours())
                    .booking(booking)
                    .description("Released hours from canceled booking: " + booking.getOffer().getTitle())
                    .build();
            transactionRepository.save(transaction);
        }
        
        Booking savedBooking = bookingRepository.save(booking);
        return bookingMapper.toDto(savedBooking);
    }
    
    @Transactional(readOnly = true)
    public BookingDto getBookingById(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));
        
        return bookingMapper.toDto(booking);
    }
    
    @Transactional(readOnly = true)
    public Page<BookingDto> getMyBookingsAsRequester(Pageable pageable) {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        
        return bookingRepository.findByRequesterId(userPrincipal.getId(), pageable)
                .map(bookingMapper::toDto);
    }
    
    @Transactional(readOnly = true)
    public Page<BookingDto> getMyBookingsAsOwner(Pageable pageable) {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        
        return bookingRepository.findByOfferOwnerId(userPrincipal.getId(), pageable)
                .map(bookingMapper::toDto);
    }
    
    @Transactional(readOnly = true)
    public Page<BookingDto> getBookingsByOffer(Long offerId, Pageable pageable) {
        return bookingRepository.findByOfferId(offerId, pageable)
                .map(bookingMapper::toDto);
    }
    
    @Transactional(readOnly = true)
    public Page<BookingDto> getBookingsByStatus(String status, Pageable pageable) {
        return bookingRepository.findByStatus(status, pageable)
                .map(bookingMapper::toDto);
    }
}
