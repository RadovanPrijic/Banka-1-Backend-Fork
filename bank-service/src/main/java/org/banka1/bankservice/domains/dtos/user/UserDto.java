package org.banka1.bankservice.domains.dtos.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.banka1.bankservice.domains.entities.Gender;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private Long id;
    private String firstName;
    private String lastName;
    private Date birthDate;
    private Gender gender;
    private String email;
    private String phoneNumber;
    private String homeAddress;
    private List<String> roles;

}
