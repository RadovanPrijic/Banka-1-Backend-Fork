package org.banka1.exchangeservice.domains.dtos.option;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.banka1.exchangeservice.domains.entities.ListingType;
import org.banka1.exchangeservice.domains.entities.OptionType;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OptionDto {

    private Long id;
    private ListingType listingType;
    private String symbol;
    private Double strike;
    private OptionType optionType;
    private LocalDate expirationDate;
    private Double ask;
    private Double bid;
    private Double price;
    private int openInterest;

}
