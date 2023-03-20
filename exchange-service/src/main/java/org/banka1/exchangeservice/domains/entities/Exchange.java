package org.banka1.exchangeservice.domains.entities;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "exchanges")
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

    @Column(name = "exc_mic_code")
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
}
