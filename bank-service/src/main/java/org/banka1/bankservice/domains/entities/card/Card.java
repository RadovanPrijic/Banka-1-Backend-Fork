package org.banka1.bankservice.domains.entities.card;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.banka1.bankservice.domains.entities.account.AccountStatus;
import org.banka1.bankservice.domains.entities.currency_exchange.ConversionTransfer;
import org.banka1.bankservice.domains.entities.payment.Payment;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "cards")
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long ownerId;

    private String accountNumber;

    @Column(unique = true)
    private String cardNumber;

    private Integer cvvCode;

    private String cardName;

    @Enumerated(EnumType.STRING)
    private CardType cardType;

    private String cardCurrencySymbol;

    @JsonFormat(pattern="dd-MM-yyyy")
    private LocalDate creationDate;

    @JsonFormat(pattern="dd-MM-yyyy")
    private LocalDate expiryDate;

    private Double cardLimit;

    private Double remainingUntilLimit;

    @JsonFormat(pattern="dd-MM-yyyy")
    private LocalDate lastLimitDate;

    @Enumerated(EnumType.STRING)
    private AccountStatus cardStatus;

    @OneToMany(mappedBy = "card")
    private List<Payment> cardPayments;

    @OneToMany(mappedBy = "card")
    private List<ConversionTransfer> cardConversions;


}
