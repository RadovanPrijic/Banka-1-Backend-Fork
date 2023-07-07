package org.banka1.bankservice.domains.mappers;

import org.banka1.bankservice.domains.dtos.credit.CreditDto;
import org.banka1.bankservice.domains.dtos.credit.CreditRequestCreateDto;
import org.banka1.bankservice.domains.dtos.credit.CreditRequestDto;
import org.banka1.bankservice.domains.dtos.credit.CreditInstallmentDto;
import org.banka1.bankservice.domains.entities.credit.Credit;
import org.banka1.bankservice.domains.entities.credit.CreditInstallment;
import org.banka1.bankservice.domains.entities.credit.CreditRequest;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CreditMapper {

    CreditMapper INSTANCE = Mappers.getMapper(CreditMapper.class);

    CreditRequestDto creditRequestToCreditRequestDto(CreditRequest creditRequest);
    CreditRequest creditRequestCreateDtoToCreditRequest(CreditRequestCreateDto creditRequestCreateDto);
    CreditDto creditToCreditDto(Credit credit);
    CreditInstallmentDto creditInstallmentToCreditInstallmentDto(CreditInstallment creditInstallment);

}
