package org.banka1.bankservice.domains.dtos.payment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentReceiverUpdateDto {

    private String receiverName;
    private String receiverAccountNumber;
    private String referenceNumber;
    private String paymentNumber;
    private String paymentPurpose;

}
