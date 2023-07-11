package org.banka1.bankservice.domains.dtos.account;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompanyCreateDto {

    @NotBlank
    private String companyName;

    @NotBlank
    private String phoneNumber;

    private String faxNumber;

    @Min(value=0, message="numericField: positive number, min 0 is required")
    private Integer vatIdNumber; // Poreski identifikacioni broj

    @Min(value=0, message="numericField: positive number, min 0 is required")
    private Integer identificationNumber; // Maticni broj

    @Min(value=0, message="numericField: positive number, min 0 is required")
    private Integer activityCode; // Sifra delatnosti

    @Min(value=0, message="numericField: positive number, min 0 is required")
    private Integer registryNumber; // Registarski broj

}
