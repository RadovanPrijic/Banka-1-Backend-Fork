package org.banka1.exchangeservice.domains.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Set;

@Entity
@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "exchanges")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Exchange {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exc_id")
    private Long excId;

    @Column(name = "exc_name")
    @NotBlank
    private String excName;

    @Column(name = "exc_acronym")
    @NotBlank
    private String excAcronym;

    @Column(name = "exc_mic_code",unique = true)
    @NotBlank
    private String excMicCode;

    @Column(name = "exc_country")
    @NotBlank
    private String excCountry;

    @Column(name = "exc_currency")
    @NotBlank
    private String excCurrency;

    @Column(name = "exc_time_zone")
    @NotBlank
    private String excTimeZone;

    @Column(name = "exc_open_time")
    @NotBlank
    private String excOpenTime;

    @Column(name = "exc_close_time")
    @NotBlank
    private String excCloseTime;

    @OneToMany(mappedBy = "exchange", cascade = {CascadeType.PERSIST})
    private Set<Forex> forexes;
    @OneToMany(mappedBy = "exchange", cascade = {CascadeType.PERSIST})
    private Set<FuturesContract> futuresContracts;
    @OneToMany(mappedBy = "exchange", cascade = {CascadeType.PERSIST})
    private Set<Stock> stocks;
}
