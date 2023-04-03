package org.banka1.userservice.controllers;

import lombok.AllArgsConstructor;
import org.banka1.userservice.domains.dtos.user.listing.UserListingCreateDto;
import org.banka1.userservice.services.UserListingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/user-listings")
@AllArgsConstructor
@CrossOrigin
public class UserListingController {

    private final UserListingService userListingService;

    @GetMapping
    public ResponseEntity<?> getListingByUser(@RequestParam Long userId) {
        return ResponseEntity.ok(userListingService.getListingsByUser(userId));
    }

    @PostMapping("/create")
    public ResponseEntity<?> createUserListing(@RequestParam Long userId, @RequestBody UserListingCreateDto userListingCreateDto) {
        return ResponseEntity.ok(userListingService.createUserListing(userId, userListingCreateDto));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateUserListing(@PathVariable Long id, @RequestParam Integer newQuantity) {
        return ResponseEntity.ok(userListingService.updateUserListing(id, newQuantity));
    }

}
