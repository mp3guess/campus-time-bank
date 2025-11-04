package com.campus.timebank.mapper;

import com.campus.timebank.dto.CreateOfferRequest;
import com.campus.timebank.dto.OfferDto;
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
public class OfferMapperImpl implements OfferMapper {

    @Override
    public OfferDto toDto(Offer offer) {
        if ( offer == null ) {
            return null;
        }

        OfferDto.OfferDtoBuilder offerDto = OfferDto.builder();

        offerDto.ownerId( offerOwnerId( offer ) );
        offerDto.id( offer.getId() );
        offerDto.title( offer.getTitle() );
        offerDto.description( offer.getDescription() );
        offerDto.hoursRate( offer.getHoursRate() );
        if ( offer.getStatus() != null ) {
            offerDto.status( offer.getStatus().name() );
        }
        offerDto.available( offer.getAvailable() );
        offerDto.createdAt( offer.getCreatedAt() );
        offerDto.updatedAt( offer.getUpdatedAt() );

        offerDto.ownerName( offer.getOwner() != null ? offer.getOwner().getFirstName() + " " + offer.getOwner().getLastName() : "Unknown" );
        offerDto.bookingCount( offer.getBookings() != null ? offer.getBookings().size() : 0 );

        return offerDto.build();
    }

    @Override
    public Offer toEntity(CreateOfferRequest request) {
        if ( request == null ) {
            return null;
        }

        Offer.OfferBuilder offer = Offer.builder();

        offer.title( request.getTitle() );
        offer.description( request.getDescription() );
        offer.hoursRate( request.getHoursRate() );

        return offer.build();
    }

    private Long offerOwnerId(Offer offer) {
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
