package org.banka1.bankservice.domains.entities.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "bank_users")
public class BankUser {

    public static final String USER_EMPLOYEE = UserRole.ROLE_EMPLOYEE.name();
    public static final String USER_CLIENT = UserRole.ROLE_CLIENT.name();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;

    private String lastName;

    @JsonFormat(pattern="dd-MM-yyyy")
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(unique = true)
    private String email;

    private String phoneNumber;

    private String homeAddress;

    @JsonIgnore
    private String password;

    @JsonIgnore
    private String secretKey;

    @Enumerated(EnumType.STRING)
    private Position position;

    @Enumerated(EnumType.STRING)
    private Department department;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roles;

    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (roles == null) {
            return Collections.singleton(new SimpleGrantedAuthority("UNCONFIRMED"));
        }
        return roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

}
