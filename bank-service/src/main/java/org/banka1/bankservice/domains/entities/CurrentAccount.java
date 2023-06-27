package org.banka1.bankservice.domains.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@EqualsAndHashCode(callSuper = false)
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "current_acc")
public class CurrentAccount {
    //Tekuci racun
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long accRegNum;//broj racuna
    @ManyToOne
    private BankUser owner;
    private Double balance;
    private Double availableBalance;

    @ManyToOne
    private BankUser createdByEmployee;

    @JsonFormat(pattern="dd-MM-yyyy")
    private LocalDate created;

    @JsonFormat(pattern="dd-MM-yyyy")
    private LocalDate expiration;

    @OneToOne
    private Currency currency;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    private Double interestRate;

    private Double maintenanceCost;
}
