package org.banka1.userservice.domains.dtos.user.listing;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.banka1.userservice.domains.entities.ListingType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserListingDto {

    private Long id;
    private ListingType listingType;
    private String symbol;
    private int quantity;

}
