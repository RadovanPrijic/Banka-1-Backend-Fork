package org.banka1.userservice.repositories;

import org.banka1.userservice.domains.entities.UserListing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserListingRepository extends JpaRepository<UserListing, Long> {



}
