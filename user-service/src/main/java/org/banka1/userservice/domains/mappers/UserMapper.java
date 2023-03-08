package org.banka1.userservice.domains.mappers;

import org.banka1.userservice.domains.dtos.user.UserCreateDto;
import org.banka1.userservice.domains.dtos.user.UserDto;
import org.banka1.userservice.domains.dtos.user.UserUpdateDto;
import org.banka1.userservice.domains.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDto userToUserDto(User user);

    User userCreateDtoToUser(UserCreateDto userCreateDto);

    void updateUserFromUserUpdateDto(@MappingTarget User user, UserUpdateDto userUpdateDto);

}
