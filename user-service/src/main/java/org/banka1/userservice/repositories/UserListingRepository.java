package org.banka1.userservice.repositories;

import org.banka1.userservice.domains.entities.ListingType;
import org.banka1.userservice.domains.entities.UserListing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface UserListingRepository extends JpaRepository<UserListing, Long> {

    List<UserListing> findByUser_Id(Long id);

    List<UserListing> findAllBySymbolInAndListingTypeAndUser_Id(Set<String> symbols, ListingType listingType, Long userId);

}
