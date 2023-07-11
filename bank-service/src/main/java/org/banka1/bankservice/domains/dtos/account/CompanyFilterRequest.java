package org.banka1.bankservice.domains.dtos.account;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.querydsl.core.BooleanBuilder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.banka1.bankservice.domains.entities.account.QCompany;

import javax.validation.constraints.Min;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompanyFilterRequest {

    private String companyName;
    private Integer vatIdNumber;
    private Integer identificationNumber;
    private Integer activityCode;

    @JsonIgnore
    QCompany qUser = QCompany.company;

    @JsonIgnore
    public BooleanBuilder getPredicate() {
        BooleanBuilder predicate = new BooleanBuilder();

        if(companyName != null)
            predicate.and(qUser.companyName.containsIgnoreCase(companyName));

        if(vatIdNumber != null)
            predicate.and(qUser.vatIdNumber.eq(vatIdNumber));

        if(identificationNumber != null)
            predicate.and(qUser.identificationNumber.eq(identificationNumber));

        if(activityCode != null)
            predicate.and(qUser.activityCode.eq(activityCode));

        return predicate;
    }

}

