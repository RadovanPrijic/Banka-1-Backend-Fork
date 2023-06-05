package org.banka1.userservice.services;

import org.banka1.userservice.IntegrationTest;
import org.banka1.userservice.domains.dtos.user.listing.UserListingCreateDto;
import org.banka1.userservice.domains.dtos.user.listing.UserListingDto;
import org.banka1.userservice.domains.entities.ListingType;
import org.banka1.userservice.domains.entities.User;
import org.banka1.userservice.domains.entities.UserListing;
import org.banka1.userservice.domains.exceptions.NotFoundExceptions;
import org.banka1.userservice.repositories.UserListingRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

public class UserListingServiceTest extends IntegrationTest {
    @Autowired
    private UserListingService userListingService;
    @Autowired
    private UserListingRepository userListingRepository;

    @Test
    public void getUserListings() {
        Long id = userRepository.findByEmail("test@test.com").get().getId();
        List<UserListingDto> userListings = userListingService.getListingsByUser(id);
        Assertions.assertEquals(0, userListings.size());
    }

    @Test
    public void createUserListing() {
        Long id = userRepository.findByEmail("supervisor@supervisor.com").get().getId();
        UserListingCreateDto userListingCreateDto = new UserListingCreateDto();
        userListingCreateDto.setListingType(ListingType.STOCK);
        userListingCreateDto.setQuantity(10);
        userListingCreateDto.setSymbol("AAPL");

        UserListingDto response = userListingService.createUserListing(id, userListingCreateDto);

        Assertions.assertNotNull(response);
    }

    @Test
    public void createUserListingNotFound() {
        UserListingCreateDto userListingCreateDto = new UserListingCreateDto();
        userListingCreateDto.setListingType(ListingType.STOCK);
        userListingCreateDto.setQuantity(10);
        userListingCreateDto.setSymbol("AAPL");

        Assertions.assertThrows(NotFoundExceptions.class,
                () -> userListingService.createUserListing(0L, userListingCreateDto), "user not found");
    }

    @Test
    public void updateUserListing() {
        User user = userRepository.findByEmail("supervisor@supervisor.com").get();
        UserListing userListing = UserListing.builder()
                .user(user)
                .listingType(ListingType.STOCK)
                .quantity(10)
                .symbol("AMZN")
                .build();
        userListing = userListingRepository.save(userListing);


        UserListingDto response = userListingService.updateUserListing(userListing.getId(), 20);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(20, response.getQuantity());
    }

    @Test
    public void updateUserListingNotFound() {
        Assertions.assertThrows(NotFoundExceptions.class,
                () -> userListingService.updateUserListing(0L, 20), "listing not found");
    }
}
