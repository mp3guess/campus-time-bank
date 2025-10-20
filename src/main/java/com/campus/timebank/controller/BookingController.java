package com.campus.timebank.controller;

import com.campus.timebank.dto.BookingDto;
import com.campus.timebank.dto.CreateBookingRequest;
import com.campus.timebank.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {
    
    private final BookingService bookingService;
    
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BookingDto> createBooking(@Valid @RequestBody CreateBookingRequest request) {
        BookingDto bookingDto = bookingService.createBooking(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingDto);
    }
    
    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDto> getBooking(@PathVariable Long bookingId) {
        BookingDto bookingDto = bookingService.getBookingById(bookingId);
        return ResponseEntity.ok(bookingDto);
    }
    
    @PutMapping("/{bookingId}/confirm")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BookingDto> confirmBooking(@PathVariable Long bookingId) {
        BookingDto bookingDto = bookingService.confirmBooking(bookingId);
        return ResponseEntity.ok(bookingDto);
    }
    
    @PutMapping("/{bookingId}/complete")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BookingDto> completeBooking(@PathVariable Long bookingId) {
        BookingDto bookingDto = bookingService.completeBooking(bookingId);
        return ResponseEntity.ok(bookingDto);
    }
    
    @PutMapping("/{bookingId}/cancel")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BookingDto> cancelBooking(
            @PathVariable Long bookingId,
            @RequestParam(required = false, defaultValue = "No reason provided") String reason) {
        BookingDto bookingDto = bookingService.cancelBooking(bookingId, reason);
        return ResponseEntity.ok(bookingDto);
    }
    
    @GetMapping("/my/as-requester")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<BookingDto>> getMyBookingsAsRequester(Pageable pageable) {
        Page<BookingDto> bookings = bookingService.getMyBookingsAsRequester(pageable);
        return ResponseEntity.ok(bookings);
    }
    
    @GetMapping("/my/as-owner")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<BookingDto>> getMyBookingsAsOwner(Pageable pageable) {
        Page<BookingDto> bookings = bookingService.getMyBookingsAsOwner(pageable);
        return ResponseEntity.ok(bookings);
    }
    
    @GetMapping("/offer/{offerId}")
    public ResponseEntity<Page<BookingDto>> getBookingsByOffer(
            @PathVariable Long offerId,
            Pageable pageable) {
        Page<BookingDto> bookings = bookingService.getBookingsByOffer(offerId, pageable);
        return ResponseEntity.ok(bookings);
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<Page<BookingDto>> getBookingsByStatus(
            @PathVariable String status,
            Pageable pageable) {
        Page<BookingDto> bookings = bookingService.getBookingsByStatus(status, pageable);
        return ResponseEntity.ok(bookings);
    }
}
