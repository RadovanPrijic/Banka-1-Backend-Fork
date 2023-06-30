package org.banka1.bankservice.domains.entities.account;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.banka1.bankservice.domains.entities.user.BankUser;
import org.banka1.bankservice.domains.entities.user.Company;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "business_accounts")
public class BusinessAccount extends Account {

    private Long companyId;

}
