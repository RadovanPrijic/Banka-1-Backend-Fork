package org.banka1.userservice.domains.mappers;

import org.banka1.userservice.domains.dtos.user.listing.UserListingCreateDto;
import org.banka1.userservice.domains.dtos.user.listing.UserListingDto;
import org.banka1.userservice.domains.entities.UserListing;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserListingMapper {

    UserListingMapper INSTANCE = Mappers.getMapper(UserListingMapper.class);

    UserListingDto userListingToUserListingDto(UserListing userListing);

    UserListing userListingCreateDtoToUserListing(UserListingCreateDto userListingCreateDto);

}
