package com.campus.timebank.mapper;

import com.campus.timebank.dto.WalletDto;
import com.campus.timebank.entity.Wallet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WalletMapper {
    
    @Mapping(target = "userId", source = "user.id")
    WalletDto toDto(Wallet wallet);
    
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Wallet toEntity(WalletDto walletDto);
}

