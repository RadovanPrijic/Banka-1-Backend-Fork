package org.banka1.bankservice.domains.entities.embeddable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class ForeignCurrencyAccCurrencyKey implements Serializable {

    @Column(name = "foreign_currency_acc_id")
    private Long ForeignCurrencyAccId;
    @Column(name = "currency_id")
    private Long currencyId;


    public ForeignCurrencyAccCurrencyKey(Long foreignCurrencyAccId, Long currencyId) {
        ForeignCurrencyAccId = foreignCurrencyAccId;
        this.currencyId = currencyId;
    }

    public ForeignCurrencyAccCurrencyKey() {

    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ForeignCurrencyAccCurrencyKey that = (ForeignCurrencyAccCurrencyKey) o;

        if (!ForeignCurrencyAccId.equals(that.ForeignCurrencyAccId)) return false;
        return currencyId.equals(that.currencyId);
    }

    @Override
    public int hashCode() {
        int result = ForeignCurrencyAccId.hashCode();
        result = 31 * result + currencyId.hashCode();
        return result;
    }
}
