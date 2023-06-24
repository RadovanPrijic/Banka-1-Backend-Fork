package org.banka1.bankservice.domains.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.persistence.*;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = false)
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users",
        indexes = { @Index(name = "email_index", columnList = "email")})
public class User {

    public static final String USER_EMPLOYEE = UserRole.ROLE_EMPLOYEE.name();
    public static final String USER_CLIENT = UserRole.ROLE_CLIENT.name();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;

    private String lastName;

    private Date birthDate;

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

    private boolean active;

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
