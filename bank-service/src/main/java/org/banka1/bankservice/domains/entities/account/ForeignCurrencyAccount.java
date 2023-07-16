package org.banka1.bankservice.domains.entities.account;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.banka1.bankservice.domains.entities.user.BankUser;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "foreign_currency_accounts")
public class ForeignCurrencyAccount extends Account {

    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    private Double interestRate;

    private Double maintenanceCost;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "account")
    private List<ForeignCurrencyBalance> foreignCurrencyBalances;

}
