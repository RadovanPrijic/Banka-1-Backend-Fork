package org.banka1.bankservice.domains.mappers;

import org.banka1.bankservice.domains.dtos.user.UserCreateDto;
import org.banka1.bankservice.domains.dtos.user.UserDto;
import org.banka1.bankservice.domains.dtos.user.UserMyProfileDto;
import org.banka1.bankservice.domains.dtos.user.UserUpdateDto;
import org.banka1.bankservice.domains.entities.user.BankUser;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDto userToUserDto(BankUser bankUser);

    BankUser userCreateDtoToUser(UserCreateDto userCreateDto);

    UserMyProfileDto userToUserMyProfileDto(BankUser bankUser);

    void updateUserFromUserUpdateDto(@MappingTarget BankUser bankUser, UserUpdateDto userUpdateDto);

}
