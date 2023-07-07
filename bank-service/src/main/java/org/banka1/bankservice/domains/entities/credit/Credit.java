package org.banka1.bankservice.domains.entities.credit;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "credits")
public class Credit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long clientId;
    private String accountNumber;
    private String creditName;
    private Double creditAmount;
    private Integer amortisationLength; // Broj meseci za otplacivanje kredita
    private Double interestRate; // Kamatna stopa u %
    private Double rateAmount;
    private LocalDate creationDate;
    private LocalDate dueDate;
    private LocalDate nextRateFirstDate; // Kamatu treba platiti u periodu od nextRateFirstDate
    private LocalDate nextRateLastDate; //  do nextRateLastDate
    private Double leftToPay;
    private String currencyCode;

}
