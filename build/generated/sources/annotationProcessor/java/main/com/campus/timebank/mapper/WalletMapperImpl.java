package com.campus.timebank.mapper;

import com.campus.timebank.dto.WalletDto;
import com.campus.timebank.entity.User;
import com.campus.timebank.entity.Wallet;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-11-24T19:43:21+0100",
    comments = "version: 1.5.5.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.4.jar, environment: Java 21.0.8 (Eclipse Adoptium)"
)
@Component
public class WalletMapperImpl implements WalletMapper {

    @Override
    public WalletDto toDto(Wallet wallet) {
        if ( wallet == null ) {
            return null;
        }

        WalletDto.WalletDtoBuilder walletDto = WalletDto.builder();

        walletDto.userId( walletUserId( wallet ) );
        walletDto.id( wallet.getId() );
        walletDto.balance( wallet.getBalance() );
        walletDto.totalEarned( wallet.getTotalEarned() );
        walletDto.totalSpent( wallet.getTotalSpent() );
        walletDto.createdAt( wallet.getCreatedAt() );
        walletDto.updatedAt( wallet.getUpdatedAt() );

        return walletDto.build();
    }

    @Override
    public Wallet toEntity(WalletDto walletDto) {
        if ( walletDto == null ) {
            return null;
        }

        Wallet.WalletBuilder wallet = Wallet.builder();

        wallet.id( walletDto.getId() );
        wallet.balance( walletDto.getBalance() );
        wallet.totalEarned( walletDto.getTotalEarned() );
        wallet.totalSpent( walletDto.getTotalSpent() );

        return wallet.build();
    }

    private Long walletUserId(Wallet wallet) {
        if ( wallet == null ) {
            return null;
        }
        User user = wallet.getUser();
        if ( user == null ) {
            return null;
        }
        Long id = user.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
