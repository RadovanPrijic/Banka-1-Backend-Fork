package org.banka1.bankservice.domains.mappers;

import org.banka1.bankservice.domains.dtos.account.*;
import org.banka1.bankservice.domains.entities.account.BusinessAccount;
import org.banka1.bankservice.domains.entities.account.CurrentAccount;
import org.banka1.bankservice.domains.entities.account.ForeignCurrencyAccount;
import org.banka1.bankservice.domains.entities.account.ForeignCurrencyBalance;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AccountMapper {

    AccountMapper INSTANCE = Mappers.getMapper(AccountMapper.class);

    CurrentAccountDto currentAccountToCurrentAccountDto(CurrentAccount currentAccount);
    ForeignCurrencyAccountDto foreignCurrencyAccountToForeignCurrencyAccountDto(ForeignCurrencyAccount foreignCurrencyAccount);
    BusinessAccountDto businessAccountToBusinessAccountDto(BusinessAccount businessAccount);
    ForeignCurrencyBalanceDto foreignCurrencyBalanceToForeignCurrencyBalanceDto(ForeignCurrencyBalance foreignCurrencyBalance);

}
