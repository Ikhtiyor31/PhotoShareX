package com.ikhtiyor.photosharex.user.controller;


import com.ikhtiyor.photosharex.user.dto.AccessTokenDTO;
import com.ikhtiyor.photosharex.user.dto.UserLoginRequest;
import com.ikhtiyor.photosharex.user.dto.UserRegisterRequest;
import com.ikhtiyor.photosharex.user.dto.UserDTO;
import com.ikhtiyor.photosharex.user.dto.PasswordResetRequest;
import com.ikhtiyor.photosharex.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService service) {
        this.userService = service;
    }

    @PostMapping(path = "/register")
    public ResponseEntity<String> register(@Valid @RequestBody UserRegisterRequest request) {
        UserDTO userDTO = userService.createUser(request);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body("User created with ID: " + userDTO.getId());
    }

    @PostMapping(path = "/login")
    public ResponseEntity<AccessTokenDTO> login(@Valid @RequestBody UserLoginRequest request) {
        AccessTokenDTO accessTokenDTO = userService.authenticateUser(request);
        return ResponseEntity.status(HttpStatus.OK)
            .body(accessTokenDTO);
    }

    @GetMapping(path = "/{userId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserDTO> getUserProfile(@PathVariable Long userId) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(userService.getUserProfile(userId));
    }

    @PostMapping(path = "/refresh/{refreshToken}")
    public ResponseEntity<AccessTokenDTO> refreshAccessToken(@PathVariable String refreshToken) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(userService.refreshAccessToken(refreshToken));
    }

    @PatchMapping(path = "/reset-password")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody PasswordResetRequest request) {
        userService.resetPassword(request);
        return ResponseEntity.status(HttpStatus.OK)
            .body("password is updated!");
    }
}
