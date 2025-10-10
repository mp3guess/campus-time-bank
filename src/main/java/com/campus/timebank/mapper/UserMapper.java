package com.campus.timebank.mapper;

import com.campus.timebank.dto.UserDto;
import com.campus.timebank.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {WalletMapper.class})
public interface UserMapper {
    
    @Mapping(target = "wallet", source = "wallet")
    UserDto toDto(User user);
    
    @Mapping(target = "wallet", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User toEntity(UserDto userDto);
}
