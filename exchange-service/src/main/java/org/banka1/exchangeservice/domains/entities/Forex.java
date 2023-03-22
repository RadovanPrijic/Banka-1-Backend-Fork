package org.banka1.exchangeservice.domains.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Set;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "forexes")
public class Forex extends Listing{

    @ManyToOne
    @JoinColumn(name = "base_currency")
    private Currency baseCurrency;
    @ManyToOne
    @JoinColumn(name = "quote_currency")
    private Currency quoteCurrency;
    @ManyToOne
    @JoinColumn(name = "exchange")
    private Exchange exchange;
    private String symbol;

}
