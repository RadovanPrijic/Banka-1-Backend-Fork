package org.banka1.bankservice.domains.dtos.payment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentReceiverCreateDto {

    @NotBlank
    private String receiverName;

    @NotBlank
    private String receiverAccountNumber;

    private String referenceNumber;

    @NotBlank
    private String paymentNumber;

    @NotBlank
    private String paymentPurpose;

}
