package org.banka1.bankservice.domains.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Set;

@EqualsAndHashCode(callSuper = false)
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "foreign_currency_acc")
public class ForeignCurrencyAccount {
    //Devizni racun
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

    private Boolean defaultCurrency;



    @Enumerated(EnumType.STRING)
    private Status status;

    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    private Double interestRate;

    private Double maintenanceCost;


    private Integer numberOfCurrenciesLimit;

    @OneToMany(mappedBy = "foreignCurrencyAccount")
    Set<ForeignCurrencyAccCurrency> currencyBalances;
}
