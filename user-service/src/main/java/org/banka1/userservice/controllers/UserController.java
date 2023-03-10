package org.banka1.userservice.controllers;

import lombok.AllArgsConstructor;
import org.banka1.userservice.domains.dtos.login.LoginRequest;
import org.banka1.userservice.domains.dtos.login.LoginResponse;
import org.banka1.userservice.domains.dtos.user.*;
import org.banka1.userservice.services.UserService;
import org.banka1.userservice.utils.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/api/users")
@AllArgsConstructor
public class UserController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest){
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        } catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(401).build();
        }

        return ResponseEntity.ok(new LoginResponse(jwtUtil.generateToken(userService.findUserByEmail(loginRequest.getEmail()))));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<?> getUsers(@RequestBody UserFilterRequest filterRequest,
                                      @RequestParam(defaultValue = "0") Integer page,
                                      @RequestParam(defaultValue = "10") Integer size) {

        return ResponseEntity.ok(userService.getUsers(filterRequest, page, size));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findUserById(id));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<?> createUser(@Valid @RequestBody UserCreateDto userCreateDto) {
        return ResponseEntity.ok(userService.createUser(userCreateDto));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateUser(@RequestBody UserUpdateDto userUpdateDto, @PathVariable Long id) {
        return ResponseEntity.ok(userService.updateUser(userUpdateDto, id));
    }

    @GetMapping("/my-profile")
    public ResponseEntity<?> aboutMe() {
        // u servisu iscupaci email iz security contex-a
        // ovako: SecurityContextHolder.getContext().getAuthentication().getName()
        return null;
    }

    @PutMapping("/my-profile/update/{id}")
    public ResponseEntity<?> updateMyself(@RequestBody UserUpdateMyProfileDto userUpdateMyProfileDto, @PathVariable Long id) {
        //ime, prezime, telefon
        return null;
    }

    @PostMapping("/reset-password/{id}")
    public ResponseEntity<?> resetPassword(@RequestBody PasswordDto passwordDto, @PathVariable Long id) {
        userService.resetUserPassword(passwordDto, id);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
        userService.forgotPassword(email);
        return ResponseEntity.ok().build();
    }

}
