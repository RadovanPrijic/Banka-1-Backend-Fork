package org.banka1.userservice.domains.mappers;

import org.banka1.userservice.domains.dtos.user.UserContractDto;
import org.banka1.userservice.domains.entities.UserContract;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserContractMapper {

    UserContractMapper INSTANCE = Mappers.getMapper(UserContractMapper.class);

    UserContract userContractDtoToUserContract(UserContractDto userContractDto);

}
