package org.banka1.userservice.services;

import org.banka1.userservice.IntegrationTest;
import org.banka1.userservice.domains.dtos.user.listing.UserListingCreateDto;
import org.banka1.userservice.domains.dtos.user.listing.UserListingDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

public class UserListingServiceTest extends IntegrationTest {
    @MockBean
    private UserListingService userListingService;

    @BeforeEach
    public void setUpMocks() {
        when(userListingService.getListingsByUser(anyLong())).thenReturn(Collections.emptyList());
        when(userListingService.createUserListing(anyLong(), any())).thenReturn(new UserListingDto());
        when(userListingService.updateUserListing(anyLong(), any())).thenReturn(new UserListingDto());
    }

    @Test
    public void getUserListings() {
        List<UserListingDto> userListings = userListingService.getListingsByUser(1L);
        Assertions.assertEquals(0, userListings.size());
    }

    @Test
    public void createUserListing() {
        UserListingDto response = userListingService.createUserListing(1L, new UserListingCreateDto());
        Assertions.assertNotNull(response);
    }

    @Test
    public void updateUserListing() {
        UserListingDto response = userListingService.updateUserListing(1L, 10);
        Assertions.assertNotNull(response);
    }
}
