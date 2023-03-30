package org.banka1.exchangeservice.domains.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "forexes")
public class Forex{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime lastRefresh;
    @ManyToOne
    private Currency fromCurrency;
    @ManyToOne
    private Currency toCurrency;

    private Double exchangeRate;

    private String symbol;

    private Double bidPrice;

    private Double askPrice;

}
