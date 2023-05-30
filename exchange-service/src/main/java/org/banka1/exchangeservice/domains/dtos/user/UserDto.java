package org.banka1.exchangeservice.domains.dtos.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String jmbg;
    private Position position;
    private String phoneNumber;
    private boolean active;
    private Double dailyLimit;

    private BankAccountDto bankAccount;

    private List<String> roles;

}
