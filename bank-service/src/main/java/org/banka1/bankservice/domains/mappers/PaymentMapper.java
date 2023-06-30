package org.banka1.bankservice.domains.mappers;

import org.banka1.bankservice.domains.dtos.payment.*;
import org.banka1.bankservice.domains.entities.payment.Payment;
import org.banka1.bankservice.domains.entities.payment.PaymentReceiver;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PaymentMapper {

    PaymentMapper INSTANCE = Mappers.getMapper(PaymentMapper.class);

    PaymentDto paymentToPaymentDto(Payment payment);
    Payment paymentCreateDtoToPayment(PaymentCreateDto paymentCreateDto);
    Payment moneyTransferDtoToPayment(MoneyTransferDto moneyTransferDto);

    PaymentReceiverDto paymentReceiverToPaymentReceiverDto(PaymentReceiver paymentReceiver);
    PaymentReceiver paymentReceiverCreateDtoToPaymentReceiver(PaymentReceiverCreateDto paymentReceiverCreateDto);
    void updatePaymentReceiverFromPaymentReceiverUpdateDto(@MappingTarget PaymentReceiver paymentReceiver, PaymentReceiverUpdateDto paymentReceiverUpdateDto);

}
