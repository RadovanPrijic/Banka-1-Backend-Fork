package org.banka1.bankservice.domains.entities;

import lombok.*;
import org.banka1.bankservice.domains.entities.embeddable.ForeignCurrencyAccCurrencyKey;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = false)
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "foreign_currency_acc_currency")
public class ForeignCurrencyAccCurrency {

    @EmbeddedId
    private ForeignCurrencyAccCurrencyKey id;

    @ManyToOne
    @MapsId("ForeignCurrencyAccId")
    @JoinColumn(name = "foreign_currency_acc_id")
    private ForeignCurrencyAccount foreignCurrencyAccount;

    @ManyToOne
    @MapsId("currencyId")
    @JoinColumn(name = "currency_id")
    private Currency currency;

    private Double balanceAmount;
}
