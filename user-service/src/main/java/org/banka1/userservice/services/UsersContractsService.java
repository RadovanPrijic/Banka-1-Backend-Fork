package org.banka1.userservice.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.banka1.userservice.domains.dtos.user.UserContractDto;
import org.banka1.userservice.domains.dtos.user.listing.UserContractListingsDto;
import org.banka1.userservice.domains.dtos.user.listing.UserListingCreateDto;
import org.banka1.userservice.domains.entities.*;
import org.banka1.userservice.domains.exceptions.NotFoundExceptions;
import org.banka1.userservice.domains.mappers.UserContractMapper;
import org.banka1.userservice.repositories.BankAccountRepository;
import org.banka1.userservice.repositories.UserContractRepository;
import org.banka1.userservice.repositories.UserListingRepository;
import org.banka1.userservice.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UsersContractsService {

    private final BankAccountRepository bankAccountRepository;
    private final UserContractRepository userContractRepository;
    private final UserListingRepository userListingRepository;
    private final UserListingService userListingService;
    private final UserRepository userRepository;


    public void createUpdateUserContract(UserContractDto userContractDto) {
        UserContract userContract = userContractRepository.findByContractId(userContractDto.getContractId());

        Double oldPrice = userContract == null ? 0 : userContract.getPrice();
        Double newPrice = userContractDto.getPrice();

        if (userContract == null)
            userContract = UserContractMapper.INSTANCE.userContractDtoToUserContract(userContractDto);
        else
            userContract.setPrice(userContractDto.getPrice());

        BankAccount bankAccount = bankAccountRepository.findAll().get(0);
        bankAccount.setReservedAsset(bankAccount.getReservedAsset() + (newPrice - oldPrice));

        bankAccountRepository.save(bankAccount);
        userContractRepository.save(userContract);
    }

    public void deleteUserContract(String contractId) {
        UserContract userContract = userContractRepository.findByContractId(contractId);
        BankAccount bankAccount = bankAccountRepository.findAll().get(0);

        bankAccount.setReservedAsset(bankAccount.getReservedAsset() - userContract.getPrice());
        bankAccountRepository.save(bankAccount);
        userContractRepository.delete(userContract);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void finalizeContract(UserContractListingsDto userContractListingsDto) {
        UserContract userContract = userContractRepository.findByContractId(userContractListingsDto.getContractId());
        Double price = userContract == null ? 0 : userContract.getPrice();

        BankAccount bankAccount = bankAccountRepository.findAll().get(0);
        bankAccount.setAccountBalance(bankAccount.getAccountBalance() + userContractListingsDto.getSellPrice() - price);
        bankAccount.setReservedAsset(bankAccount.getReservedAsset() - price);

        Set<String> stockSymbols = userContractListingsDto.getStocks()
                .stream().map(UserContractListingsDto.StockData::getSymbol).collect(Collectors.toSet());

        List<UserListing> userListings = userListingRepository
                .findAllBySymbolInAndListingTypeAndUser_Id(stockSymbols, ListingType.STOCK, userContractListingsDto.getUserId());
        Map<String, UserListing> listingMap = userListings.stream().collect(Collectors.toMap(UserListing::getSymbol, Function.identity()));

        User user = userRepository.findById(userContractListingsDto.getUserId()).orElseThrow(() -> new NotFoundExceptions("user not found"));
        userContractListingsDto.getStocks().forEach(el -> {
            UserListing ul = listingMap.get(el.getSymbol());
            if (ul == null && el.getTransactionType() == UserContractListingsDto.TransactionType.BUY) {
                ul = UserListing.builder()
                        .listingType(ListingType.STOCK)
                        .symbol(el.getSymbol())
                        .user(user)
                        .quantity(el.getQuantity())
                        .build();

                listingMap.put(el.getSymbol(), ul);
            } else if (ul != null) {
                switch (el.getTransactionType()) {
                    case BUY -> ul.setQuantity(ul.getQuantity() + el.getQuantity());
                    case SELL -> ul.setQuantity(Math.max(ul.getQuantity() - el.getQuantity(), 0));
                }
            }
        });

        if (userContract != null)
            userContractRepository.delete(userContract);

        userListingRepository.saveAll(listingMap.values());
        bankAccountRepository.save(bankAccount);
    }
}
