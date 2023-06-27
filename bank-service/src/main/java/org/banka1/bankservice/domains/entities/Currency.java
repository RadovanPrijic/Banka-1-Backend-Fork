package org.banka1.bankservice.domains.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.mapstruct.EnumMapping;

import javax.persistence.*;
import java.util.Set;

@EqualsAndHashCode(callSuper = false)
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bank_currencies")
public class Currency {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String currencyName;
    @Column(unique = true)
    private String currencyCode;//Oznaka
    private String currencySymbol;//Simbol
    private String country;
    private String description;
    @Enumerated(EnumType.STRING)
    private Status status;

    @OneToMany(mappedBy = "currency")
    @JsonIgnore
    Set<ForeignCurrencyAccCurrency> currencyBalances;

}
