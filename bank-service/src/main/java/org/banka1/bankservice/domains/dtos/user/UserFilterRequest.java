package org.banka1.bankservice.domains.dtos.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.querydsl.core.BooleanBuilder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.banka1.bankservice.domains.entities.user.Position;
import org.banka1.bankservice.domains.entities.user.QBankUser;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserFilterRequest {

    private String email;
    private String firstName;
    private String lastName;

    @JsonFormat(pattern="dd-MM-yyyy")
    private LocalDate birthDate;

    @JsonIgnore
    QBankUser qBankUser = QBankUser.bankUser;

    @JsonIgnore
    public BooleanBuilder getPredicate() {
        BooleanBuilder predicate = new BooleanBuilder();

        if(email != null)
            predicate.and(qBankUser.email.containsIgnoreCase(email));

        if(firstName != null)
            predicate.and(qBankUser.firstName.containsIgnoreCase(firstName));

        if(lastName != null)
            predicate.and(qBankUser.lastName.containsIgnoreCase(lastName));

        if(birthDate != null)
            predicate.and(qBankUser.birthDate.eq(birthDate));

        return predicate;
    }

}
