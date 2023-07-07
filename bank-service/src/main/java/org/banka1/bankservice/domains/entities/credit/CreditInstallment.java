package org.banka1.bankservice.domains.entities.credit;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "credit_installments")
public class CreditInstallment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long creditId;
    private Long clientId;

    @JsonFormat(pattern="dd-MM-yyyy HH:mm:ss")
    private LocalDateTime creditInstallmentPaymentTime;

    private Double creditInstallmentAmount;
    private Double interestRateAmount;

}
