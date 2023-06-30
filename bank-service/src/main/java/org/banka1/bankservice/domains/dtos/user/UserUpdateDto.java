package org.banka1.bankservice.domains.dtos.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.banka1.bankservice.domains.entities.user.Gender;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateDto {

    private String lastName;
    private Gender gender;
    private String phoneNumber;
    private String homeAddress;
    private String password;
    private List<String> roles;

}
