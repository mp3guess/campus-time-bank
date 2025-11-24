package com.campus.timebank.repository;

import com.campus.timebank.entity.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    
    Page<Booking> findByOfferId(Long offerId, Pageable pageable);
    
    Page<Booking> findByRequesterId(Long requesterId, Pageable pageable);
    
    Page<Booking> findByStatus(String status, Pageable pageable);
    
    Page<Booking> findByOfferIdAndStatus(Long offerId, String status, Pageable pageable);
    
    Page<Booking> findByRequesterIdAndStatus(Long requesterId, String status, Pageable pageable);
    
    List<Booking> findByOfferIdAndStatusIn(Long offerId, List<String> statuses);
    
    Optional<Booking> findByIdAndRequesterId(Long bookingId, Long requesterId);
    
    Optional<Booking> findByIdAndOfferOwnerId(Long bookingId, Long ownerId);
    
    @Query("SELECT b FROM Booking b WHERE b.offer.id = :offerId AND b.status = 'CONFIRMED'")
    List<Booking> findConfirmedBookingsByOfferId(@Param("offerId") Long offerId);
    
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.requester.id = :requesterId AND b.status = 'COMPLETED'")
    Long countCompletedBookingsForUser(@Param("requesterId") Long requesterId);
    
    @Query("SELECT b FROM Booking b JOIN b.offer o WHERE o.owner.id = :ownerId")
    Page<Booking> findByOfferOwnerId(@Param("ownerId") Long ownerId, Pageable pageable);
}
