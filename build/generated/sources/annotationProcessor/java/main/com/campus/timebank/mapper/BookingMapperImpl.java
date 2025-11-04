package com.campus.timebank.mapper;

import com.campus.timebank.dto.BookingDto;
import com.campus.timebank.entity.Booking;
import com.campus.timebank.entity.Offer;
import com.campus.timebank.entity.User;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-11-04T00:09:25+0100",
    comments = "version: 1.5.5.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.14.1.jar, environment: Java 17.0.12 (Eclipse Adoptium)"
)
@Component
public class BookingMapperImpl implements BookingMapper {

    @Override
    public BookingDto toDto(Booking booking) {
        if ( booking == null ) {
            return null;
        }

        BookingDto.BookingDtoBuilder bookingDto = BookingDto.builder();

        bookingDto.offerId( bookingOfferId( booking ) );
        bookingDto.offerTitle( bookingOfferTitle( booking ) );
        bookingDto.requesterId( bookingRequesterId( booking ) );
        bookingDto.ownerId( bookingOfferOwnerId( booking ) );
        bookingDto.id( booking.getId() );
        bookingDto.reservedHours( booking.getReservedHours() );
        bookingDto.transferredHours( booking.getTransferredHours() );
        bookingDto.cancelReason( booking.getCancelReason() );
        bookingDto.createdAt( booking.getCreatedAt() );
        bookingDto.confirmedAt( booking.getConfirmedAt() );
        bookingDto.completedAt( booking.getCompletedAt() );
        bookingDto.canceledAt( booking.getCanceledAt() );
        bookingDto.updatedAt( booking.getUpdatedAt() );

        bookingDto.requesterName( booking.getRequester().getFirstName() + " " + booking.getRequester().getLastName() );
        bookingDto.ownerName( booking.getOffer().getOwner().getFirstName() + " " + booking.getOffer().getOwner().getLastName() );
        bookingDto.status( booking.getStatus().name() );

        return bookingDto.build();
    }

    @Override
    public Booking toEntity(BookingDto bookingDto) {
        if ( bookingDto == null ) {
            return null;
        }

        Booking.BookingBuilder booking = Booking.builder();

        booking.reservedHours( bookingDto.getReservedHours() );

        return booking.build();
    }

    private Long bookingOfferId(Booking booking) {
        if ( booking == null ) {
            return null;
        }
        Offer offer = booking.getOffer();
        if ( offer == null ) {
            return null;
        }
        Long id = offer.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String bookingOfferTitle(Booking booking) {
        if ( booking == null ) {
            return null;
        }
        Offer offer = booking.getOffer();
        if ( offer == null ) {
            return null;
        }
        String title = offer.getTitle();
        if ( title == null ) {
            return null;
        }
        return title;
    }

    private Long bookingRequesterId(Booking booking) {
        if ( booking == null ) {
            return null;
        }
        User requester = booking.getRequester();
        if ( requester == null ) {
            return null;
        }
        Long id = requester.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private Long bookingOfferOwnerId(Booking booking) {
        if ( booking == null ) {
            return null;
        }
        Offer offer = booking.getOffer();
        if ( offer == null ) {
            return null;
        }
        User owner = offer.getOwner();
        if ( owner == null ) {
            return null;
        }
        Long id = owner.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
