package com.campus.timebank.mapper;

import com.campus.timebank.dto.BookingDto;
import com.campus.timebank.entity.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BookingMapper {
    
    @Mapping(target = "offerId", source = "offer.id")
    @Mapping(target = "offerTitle", source = "offer.title")
    @Mapping(target = "requesterId", source = "requester.id")
    @Mapping(target = "requesterName", expression = "java(booking.getRequester().getFirstName() + \" \" + booking.getRequester().getLastName())")
    @Mapping(target = "ownerId", source = "offer.owner.id")
    @Mapping(target = "ownerName", expression = "java(booking.getOffer().getOwner().getFirstName() + \" \" + booking.getOffer().getOwner().getLastName())")
    @Mapping(target = "status", expression = "java(booking.getStatus().name())")
    BookingDto toDto(Booking booking);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "offer", ignore = true)
    @Mapping(target = "requester", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "transferredHours", ignore = true)
    @Mapping(target = "cancelReason", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "confirmedAt", ignore = true)
    @Mapping(target = "completedAt", ignore = true)
    @Mapping(target = "canceledAt", ignore = true)
    Booking toEntity(BookingDto bookingDto);
}
