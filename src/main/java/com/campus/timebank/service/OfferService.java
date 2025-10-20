package com.campus.timebank.service;

import com.campus.timebank.dto.CreateOfferRequest;
import com.campus.timebank.dto.OfferDto;
import com.campus.timebank.entity.Offer;
import com.campus.timebank.entity.User;
import com.campus.timebank.mapper.OfferMapper;
import com.campus.timebank.repository.OfferRepository;
import com.campus.timebank.repository.UserRepository;
import com.campus.timebank.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OfferService {
    
    private final OfferRepository offerRepository;
    private final UserRepository userRepository;
    private final OfferMapper offerMapper;
    
    @Transactional
    public OfferDto createOffer(CreateOfferRequest request) {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        
        User owner = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        Offer offer = offerMapper.toEntity(request);
        offer.setOwner(owner);
        offer.setStatus(Offer.OfferStatus.ACTIVE);
        offer.setAvailable(true);
        
        Offer savedOffer = offerRepository.save(offer);
        return offerMapper.toDto(savedOffer);
    }
    
    @Transactional(readOnly = true)
    public OfferDto getOfferById(Long offerId) {
        Offer offer = offerRepository.findById(offerId)
                .orElseThrow(() -> new IllegalArgumentException("Offer not found with id: " + offerId));
        
        return offerMapper.toDto(offer);
    }
    
    @Transactional(readOnly = true)
    public Page<OfferDto> getActiveOffers(Pageable pageable) {
        return offerRepository.findByStatusAndAvailableTrue("ACTIVE", pageable)
                .map(offerMapper::toDto);
    }
    
    @Transactional(readOnly = true)
    public Page<OfferDto> getOffersByOwner(Long ownerId, Pageable pageable) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        return offerRepository.findByOwner(owner, pageable)
                .map(offerMapper::toDto);
    }
    
    @Transactional(readOnly = true)
    public Page<OfferDto> getAllOffers(Pageable pageable) {
        return offerRepository.findAll(pageable)
                .map(offerMapper::toDto);
    }
    
    @Transactional(readOnly = true)
    public List<OfferDto> getMyOffers() {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        
        return offerRepository.findByOwnerId(userPrincipal.getId()).stream()
                .map(offerMapper::toDto)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public OfferDto updateOffer(Long offerId, CreateOfferRequest request) {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        
        Offer offer = offerRepository.findByIdAndOwnerId(offerId, userPrincipal.getId())
                .orElseThrow(() -> new IllegalArgumentException("Offer not found or you don't have permission to update it"));
        
        offer.setTitle(request.getTitle());
        offer.setDescription(request.getDescription());
        offer.setHoursRate(request.getHoursRate());
        
        Offer updatedOffer = offerRepository.save(offer);
        return offerMapper.toDto(updatedOffer);
    }
    
    @Transactional
    public void deactivateOffer(Long offerId) {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        
        Offer offer = offerRepository.findByIdAndOwnerId(offerId, userPrincipal.getId())
                .orElseThrow(() -> new IllegalArgumentException("Offer not found or you don't have permission"));
        
        offer.deactivate();
        offerRepository.save(offer);
    }
    
    @Transactional
    public void activateOffer(Long offerId) {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        
        Offer offer = offerRepository.findByIdAndOwnerId(offerId, userPrincipal.getId())
                .orElseThrow(() -> new IllegalArgumentException("Offer not found or you don't have permission"));
        
        offer.activate();
        offerRepository.save(offer);
    }
}
