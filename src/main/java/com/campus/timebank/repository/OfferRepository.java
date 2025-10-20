package com.campus.timebank.repository;

import com.campus.timebank.entity.Offer;
import com.campus.timebank.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OfferRepository extends JpaRepository<Offer, Long> {
    
    Page<Offer> findByStatus(String status, Pageable pageable);
    
    Page<Offer> findByAvailableTrue(Pageable pageable);
    
    Page<Offer> findByOwner(User owner, Pageable pageable);
    
    Page<Offer> findByStatusAndAvailableTrue(String status, Pageable pageable);
    
    List<Offer> findByOwnerId(Long ownerId);
    
    Optional<Offer> findByIdAndOwnerId(Long offerId, Long ownerId);
    
    @Query("SELECT o FROM Offer o WHERE o.status = 'ACTIVE' AND o.available = true ORDER BY o.createdAt DESC")
    List<Offer> findAllActiveOffers();
    
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.offer.id = :offerId AND b.status IN ('PENDING', 'CONFIRMED')")
    Long countPendingBookings(@Param("offerId") Long offerId);
}
