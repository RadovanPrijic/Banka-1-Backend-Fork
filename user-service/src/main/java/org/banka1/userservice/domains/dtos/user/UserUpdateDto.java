package org.banka1.userservice.domains.dtos.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.banka1.userservice.domains.entities.Position;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateDto {

    private String firstName;
    private String lastName;
    private String email;
    private String jmbg;
    private Position position;
    private String phoneNumber;
    private boolean active;

    private List<String> roles;

}
