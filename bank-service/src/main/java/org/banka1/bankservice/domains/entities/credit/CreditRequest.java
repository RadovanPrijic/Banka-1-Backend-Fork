package org.banka1.bankservice.domains.entities.credit;

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
@Table(name = "credit_requests")
public class CreditRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String clientEmail;
    private String accountNumber;

    @Enumerated(EnumType.STRING)
    private CreditRequestStatus creditRequestStatus;

    @Enumerated(EnumType.STRING)
    private CreditType creditType;

    private Double creditAmount;
    private Double interestRate;
    private String creditPurpose;
    private Double monthlySalary;
    private boolean employedFullTime;
    private Integer currentEmploymentDuration;
    private String educationLevel;
    private Integer monthsToPayOff;
    private String branchOffice;
    private String phoneNumber;

}
