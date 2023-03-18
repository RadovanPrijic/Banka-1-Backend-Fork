package org.banka1.exchangeservice.domains.entities;

import lombok.*;

import javax.persistence.*;

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

    @Column(unique = true)
    private String currencySymbol;

    private String polity;

}

