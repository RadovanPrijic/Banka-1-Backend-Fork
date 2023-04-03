package org.banka1.exchangeservice.domains.dtos.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.banka1.exchangeservice.domains.entities.ListingType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserListingDto {

    private Long id;
    private ListingType listingType;
    private String symbol;
    private int quantity;

}
