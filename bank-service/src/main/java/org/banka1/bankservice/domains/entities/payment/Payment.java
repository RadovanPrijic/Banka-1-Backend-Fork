package org.banka1.bankservice.domains.entities.payment;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long senderId;
    private String receiverName;
    private String senderAccountNumber;
    private String receiverAccountNumber;
    private Double amount;

    @JsonFormat(pattern="dd-MM-yyyy HH:mm:ss")
    private LocalDateTime paymentTime;

    private String referenceNumber;
    private String paymentNumber;
    private String paymentPurpose;
    private String currencySymbol;

}
