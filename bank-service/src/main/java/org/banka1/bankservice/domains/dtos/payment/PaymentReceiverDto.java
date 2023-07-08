package org.banka1.bankservice.domains.dtos.payment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentReceiverDto {

    private Long id;
    private Long senderId;
    private String receiverName;
    private String receiverAccountNumber;
    private String referenceNumber;
    private String paymentNumber;
    private String paymentPurpose;

}
