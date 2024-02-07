package com.ikhtiyor.photosharex.user.controller;


import com.ikhtiyor.photosharex.user.dto.AccessTokenDTO;
import com.ikhtiyor.photosharex.user.dto.UserLoginRequest;
import com.ikhtiyor.photosharex.user.dto.UserResgisterRequest;
import com.ikhtiyor.photosharex.user.dto.UserDTO;
import com.ikhtiyor.photosharex.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<String> register(@Valid @RequestBody UserResgisterRequest request) {
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

}
