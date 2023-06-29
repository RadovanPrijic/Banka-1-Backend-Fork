package org.banka1.bankservice.domains.dtos.payment;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentCreateDto {

    @NotBlank
    private String receiverName;

    @NotBlank
    private String senderAccountNumber;

    @NotBlank
    private String receiverAccountNumber;

    @DecimalMin(value="0.0", message="decimalField: positive number, min 0.0 is required")
    private Double amount;

    @JsonFormat(pattern="dd-MM-yyyy HH:mm:ss")
    private LocalDateTime paymentTime;

    private String referenceNumber;

    @NotBlank
    private String paymentNumber;

    @NotBlank
    private String paymentPurpose;

}
