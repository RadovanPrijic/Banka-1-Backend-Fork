package org.banka1.bankservice.domains.entities.payment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "payment_receivers")
public class PaymentReceiver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long senderId;
    private String receiverName;
    private String receiverAccountNumber;
    private String referenceNumber;
    private String paymentNumber;
    private String paymentPurpose;

}
