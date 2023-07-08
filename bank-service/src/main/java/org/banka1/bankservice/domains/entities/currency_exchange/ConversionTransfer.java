package org.banka1.bankservice.domains.entities.currency_exchange;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "conversion_transfers")
public class ConversionTransfer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long senderId;
    private String senderAccountNumber;
    private String receiverAccountNumber;
    private String exchangePairSymbol;

    @JsonFormat(pattern="dd-MM-yyyy HH:mm:ss")
    private LocalDateTime conversionTime;

    private Double amount;
    private Double convertedAmount;
    private Double exchangeRate;
    private Double commissionFee;

}
