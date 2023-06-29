package org.banka1.bankservice.domains.dtos.payment;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDto {

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
