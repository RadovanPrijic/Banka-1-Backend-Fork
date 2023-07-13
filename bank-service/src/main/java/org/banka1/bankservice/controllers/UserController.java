package org.banka1.bankservice.controllers;

import lombok.AllArgsConstructor;
import org.banka1.bankservice.domains.dtos.login.LoginRequest;
import org.banka1.bankservice.domains.dtos.login.LoginResponse;
import org.banka1.bankservice.domains.dtos.user.PasswordDto;
import org.banka1.bankservice.domains.dtos.user.UserCreateDto;
import org.banka1.bankservice.domains.dtos.user.UserFilterRequest;
import org.banka1.bankservice.domains.dtos.user.UserUpdateDto;
import org.banka1.bankservice.services.UserService;
import org.banka1.bankservice.utils.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/api/bank")
@AllArgsConstructor
@CrossOrigin
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

    @GetMapping("/user/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findUserById(id));
    }

    @PostMapping("/register")
    public ResponseEntity<?> createUser(@Valid @RequestBody UserCreateDto userCreateDto) {
        return ResponseEntity.ok(userService.createUser(userCreateDto));
    }

    @PreAuthorize("hasRole('ROLE_EMPLOYEE')")
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateUser(@RequestBody UserUpdateDto userUpdateDto, @PathVariable Long id) {
        return ResponseEntity.ok(userService.updateUser(userUpdateDto, id));
    }

    @PreAuthorize("hasRole('ROLE_EMPLOYEE')")
    @GetMapping("/clients")
    public ResponseEntity<?> getAllClients() {
        return ResponseEntity.ok(userService.findAllClients());
    }

    @PreAuthorize("hasRole('ROLE_EMPLOYEE')")
    @PostMapping("/clients_filtered")
    public ResponseEntity<?> getAllClientsFiltered(@RequestBody UserFilterRequest userFilterRequest) {
        return ResponseEntity.ok(userService.findAllClientsFiltered(userFilterRequest));
    }

    @GetMapping("/my-profile")
    public ResponseEntity<?> aboutMe() {
        return ResponseEntity.ok(userService.returnUserProfile());
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
