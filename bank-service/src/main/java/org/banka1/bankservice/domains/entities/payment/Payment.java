package org.banka1.bankservice.domains.entities.payment;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDate;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
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

}
