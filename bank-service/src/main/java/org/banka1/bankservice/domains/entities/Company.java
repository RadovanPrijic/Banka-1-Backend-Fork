package org.banka1.bankservice.domains.entities;

import lombok.*;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = false)
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "business_acc")
public class Company {
    //Firma
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String companyName;
    private Integer phoneNumber;
    private Integer faxNumber;
    private Integer vatIdNumber; //pib
    private Integer idNumber; //maticni broj
    private Integer activityCode; //sifra delatnosti
    private Integer registrationNumber; //registarski broj

}
