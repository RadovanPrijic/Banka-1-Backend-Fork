package org.banka1.userservice.domains.dtos.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.banka1.userservice.domains.entities.Position;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserFilterRequest {

    private String firstName;
    private String lastName;
    private String email;
    private String position;

}
