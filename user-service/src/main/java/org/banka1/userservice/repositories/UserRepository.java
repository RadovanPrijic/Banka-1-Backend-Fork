package org.banka1.userservice.repositories;

import org.banka1.userservice.domains.entities.Position;
import org.banka1.userservice.domains.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, PagingAndSortingRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Page<User> findByFirstNameContainingIgnoreCaseAndLastNameContainingIgnoreCaseAndEmailContainingIgnoreCaseAndPositionIs(String firstName, String lastName, String email, Position position, Pageable pageable);
    Page<User> findByFirstNameContainingIgnoreCaseAndLastNameContainingIgnoreCaseAndEmailContainingIgnoreCase(String firstName, String lastName, String email, Pageable pageable);
}
