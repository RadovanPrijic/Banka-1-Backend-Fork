package org.banka1.bankservice.domains.dtos.account;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompanyDto {

    private Long id;
    private String companyName;
    private String phoneNumber;
    private String faxNumber;
    private Integer vatIdNumber; // Poreski identifikacioni broj
    private Integer identificationNumber; // Maticni broj
    private Integer activityCode; // Sifra delatnosti
    private Integer registryNumber; // Registarski broj

}
