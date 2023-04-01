package org.banka1.exchangeservice.domains.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.util.Date;


@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private String email;

    @Enumerated(value = EnumType.STRING)
    private OrderAction orderAction;
    @Enumerated(value = EnumType.STRING)
    private OrderType orderType;
    @Enumerated(value = EnumType.STRING)
    private OrderStatus orderStatus;

    @Enumerated(value = EnumType.STRING)
    private ListingType listingType;
    private String listingSymbol;

    private Integer quantity;
    private Integer remainingQuantity;
//    private Integer backoff = -1; ???

    private Double limitValue;
    private Double stopValue;

    @Transient
    private Double expectedPrice;

    private boolean allOrNone;
    private boolean margin;

    // Koriste se samo pri izracunavanju, ne perzistujemo vrednosti
    @Transient
    private Double ask;
    @Transient
    private Double bid;

    @ColumnDefault("false")
    private Boolean done = false;

    private Date lastModified;

}
