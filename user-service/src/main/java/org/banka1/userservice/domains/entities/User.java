package org.banka1.userservice.domains.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.persistence.*;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = false)
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users",
        indexes = { @Index(name = "email_index", columnList = "email"),
                    @Index(name = "jmbg_index", columnList = "jmbg") })
public class User {

    public static final String USER_ADMIN = UserRole.ROLE_ADMIN.name();
    public static final String USER_MODERATOR = UserRole.ROLE_MODERATOR.name();
    public static final String USER_SUPERVISOR = UserRole.ROLE_SUPERVISOR.name();
    public static final String USER_AGENT = UserRole.ROLE_AGENT.name();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String jmbg;

    @Enumerated(EnumType.STRING)
    private Position position;

    private String phoneNumber;

    @JsonIgnore
    private String password;
    @JsonIgnore
    private String secretKey;

    private boolean active;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roles;

    @ManyToOne
    private BankAccount bankAccount;

    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (roles == null) {
            return Collections.singleton(new SimpleGrantedAuthority("UNCONFIRMED"));
        }
        return roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

}
