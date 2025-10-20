package com.campus.timebank.controller;

import com.campus.timebank.dto.CreateOfferRequest;
import com.campus.timebank.dto.OfferDto;
import com.campus.timebank.service.OfferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/offers")
@RequiredArgsConstructor
public class OfferController {
    
    private final OfferService offerService;
    
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<OfferDto> createOffer(@Valid @RequestBody CreateOfferRequest request) {
        OfferDto offerDto = offerService.createOffer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(offerDto);
    }
    
    @GetMapping("/{offerId}")
    public ResponseEntity<OfferDto> getOffer(@PathVariable Long offerId) {
        OfferDto offerDto = offerService.getOfferById(offerId);
        return ResponseEntity.ok(offerDto);
    }
    
    @GetMapping("/active/list")
    public ResponseEntity<Page<OfferDto>> getActiveOffers(Pageable pageable) {
        Page<OfferDto> offers = offerService.getActiveOffers(pageable);
        return ResponseEntity.ok(offers);
    }
    
    @GetMapping
    public ResponseEntity<Page<OfferDto>> getAllOffers(Pageable pageable) {
        Page<OfferDto> offers = offerService.getAllOffers(pageable);
        return ResponseEntity.ok(offers);
    }
    
    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<Page<OfferDto>> getOffersByOwner(
            @PathVariable Long ownerId,
            Pageable pageable) {
        Page<OfferDto> offers = offerService.getOffersByOwner(ownerId, pageable);
        return ResponseEntity.ok(offers);
    }
    
    @GetMapping("/my-offers")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<OfferDto>> getMyOffers() {
        List<OfferDto> offers = offerService.getMyOffers();
        return ResponseEntity.ok(offers);
    }
    
    @PutMapping("/{offerId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<OfferDto> updateOffer(
            @PathVariable Long offerId,
            @Valid @RequestBody CreateOfferRequest request) {
        OfferDto offerDto = offerService.updateOffer(offerId, request);
        return ResponseEntity.ok(offerDto);
    }
    
    @PutMapping("/{offerId}/deactivate")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deactivateOffer(@PathVariable Long offerId) {
        offerService.deactivateOffer(offerId);
        return ResponseEntity.noContent().build();
    }
    
    @PutMapping("/{offerId}/activate")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> activateOffer(@PathVariable Long offerId) {
        offerService.activateOffer(offerId);
        return ResponseEntity.noContent().build();
    }
}
