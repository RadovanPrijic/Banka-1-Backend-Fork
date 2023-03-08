package org.banka1.userservice.domains.dtos.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PasswordDto {

    @NotBlank
    private String password;
    @NotBlank
    private String secretKey;

}
