package org.banka1.userservice.services;

import lombok.extern.slf4j.Slf4j;
import org.banka1.userservice.domains.dtos.user.listing.UserListingCreateDto;
import org.banka1.userservice.domains.dtos.user.listing.UserListingDto;
import org.banka1.userservice.domains.entities.User;
import org.banka1.userservice.domains.entities.UserListing;
import org.banka1.userservice.domains.exceptions.NotFoundExceptions;
import org.banka1.userservice.domains.mappers.UserListingMapper;
import org.banka1.userservice.domains.mappers.UserMapper;
import org.banka1.userservice.repositories.UserListingRepository;
import org.banka1.userservice.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserListingService {

    private final UserListingRepository userListingRepository;
    private final UserRepository userRepository;

    public UserListingService(UserListingRepository userListingRepository, UserRepository userRepository) {
        this.userListingRepository = userListingRepository;
        this.userRepository = userRepository;
    }

    public List<UserListingDto> getListingsByUser(Long userId) {
        return userListingRepository.findByUser_Id(userId).stream().map(UserListingMapper.INSTANCE::userListingToUserListingDto).collect(Collectors.toList());
    }

    public UserListingDto createUserListing(Long userId, UserListingCreateDto userListingCreateDto) {
        UserListing userListing = UserListingMapper.INSTANCE.userListingCreateDtoToUserListing(userListingCreateDto);
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundExceptions("user not found"));

        userListing.setUser(user);
        userListing = userListingRepository.save(userListing);

        return UserListingMapper.INSTANCE.userListingToUserListingDto(userListing);
    }

    public UserListingDto updateUserListing(Long id, Integer newQuantity) {
        UserListing userListing = userListingRepository.findById(id).orElseThrow(() -> new NotFoundExceptions("listing not found"));
        userListing.setQuantity(newQuantity);
        userListing = userListingRepository.save(userListing);
        return UserListingMapper.INSTANCE.userListingToUserListingDto(userListing);
    }
}
