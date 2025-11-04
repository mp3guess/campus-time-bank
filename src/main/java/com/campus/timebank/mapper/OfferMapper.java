package com.campus.timebank.mapper;

import com.campus.timebank.dto.CreateOfferRequest;
import com.campus.timebank.dto.OfferDto;
import com.campus.timebank.entity.Offer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OfferMapper {
    
    @Mapping(target = "ownerId", source = "owner.id")
    @Mapping(target = "ownerName", expression = "java(offer.getOwner() != null ? offer.getOwner().getFirstName() + \" \" + offer.getOwner().getLastName() : \"Unknown\")")
    @Mapping(target = "bookingCount", expression = "java(offer.getBookings() != null ? offer.getBookings().size() : 0)")
    OfferDto toDto(Offer offer);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "bookings", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "available", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Offer toEntity(CreateOfferRequest request);
}
