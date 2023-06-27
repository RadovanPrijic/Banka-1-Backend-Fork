package org.banka1.bankservice.domains.entities.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "companies")
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String companyName;

    private String phoneNumber;

    private String faxNumber;

    private Integer vatIdNumber; // Poreski identifikacioni broj

    private Integer identificationNumber; // Maticni broj

    private Integer activityCode; // Sifra delatnosti

    private Integer registryNumber; // Registarski broj

}
