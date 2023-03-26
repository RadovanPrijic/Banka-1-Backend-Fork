package org.banka1.exchangeservice.domains.entities;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@EqualsAndHashCode(callSuper = false)
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "currencies")
public class Currency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String currencyName;

    @Column(unique = true)
    private String currencyCode;

    private String currencySymbol;

    private String polity;


    @OneToMany(mappedBy = "baseCurrency")
    private Set<Forex> baseForexes;
    @OneToMany(mappedBy = "quoteCurrency")
    private Set<Forex> quoteForexes;
}

