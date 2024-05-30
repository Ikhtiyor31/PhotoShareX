package com.ikhtiyor.photosharex.user.controller;

import static com.google.common.net.HttpHeaders.CONTENT_TYPE;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ikhtiyor.photosharex.IntegrationTestSetup;
import com.ikhtiyor.photosharex.user.dto.AccessTokenDTO;
import com.ikhtiyor.photosharex.user.dto.PasswordResetRequest;
import com.ikhtiyor.photosharex.user.dto.UserDTO;
import com.ikhtiyor.photosharex.user.dto.UserLoginRequest;
import com.ikhtiyor.photosharex.user.dto.UserRegisterRequest;
import com.ikhtiyor.photosharex.user.dto.VerificationCodeRequest;
import com.ikhtiyor.photosharex.user.model.User;
import com.ikhtiyor.photosharex.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(UserController.class)
class UserControllerTest extends IntegrationTestSetup {

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String USER_API_BASE_URL = "/api/v1/users";

    @Test
    void createUser_ReturnsUserDTO_ResponseEntity() throws Exception {
        var mockUserDTO = new UserDTO(1L, "ikhtiyor", "ikhtiyor@gmail.com");
        UserRegisterRequest request = new UserRegisterRequest(
            "ikhtiyor",
            "new@example.com",
            "password",
            "my-profile-image.jpg"
        );
        when(userService.createUser(request)).thenReturn(mockUserDTO);

        mockMvc.perform(post(USER_API_BASE_URL + "/register")
                .header(CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .header(ACCEPT, APPLICATION_JSON_VALUE)
                .content(new ObjectMapper().writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(content().string("User created with ID: " + mockUserDTO.getId()));
    }

    @Test
    void shouldAuthenticateUser_AndReturnToken() throws Exception {
        String email = "fake@gmail.com";
        String password = "password1234";
        var userLoginRequest = new UserLoginRequest(email, password);
        var tokenDto = new AccessTokenDTO("access-token", "refresh-token");
        given(userService.authenticateUser(userLoginRequest)).willReturn(tokenDto);

        mockMvc.perform(post(USER_API_BASE_URL + "/login")
                .header(CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .header(ACCEPT, APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsBytes(userLoginRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("accessToken").value(tokenDto.accessToken()))
            .andExpect(jsonPath("refreshToken").value(tokenDto.refreshToken()));
    }

    @Test
    void shouldVerifyEmail_whenRequestValid() throws Exception {
        String email = "example@gmail.com";
        String verificationCode = "1234";
        var verificationCodeRequest = new VerificationCodeRequest(email, verificationCode);
        userService.verifyEmail(verificationCodeRequest);

        mockMvc.perform(post(USER_API_BASE_URL + "/verify-email")
                .header(CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .header(ACCEPT, APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsBytes(verificationCodeRequest)))
            .andExpect(status().isOk())
            .andExpect(content().string("Ok"));
    }

    @Test
    @WithMockUser(roles = "ADMI")
    void getUserProfileTest() throws Exception {
        Long userId = 1L;
        UserDTO mockUserDTO = new UserDTO(userId, "ikhtiyor", "ikhtiyor@gmail.com");
        var mockUser = new User(mockUserDTO.getName(), mockUserDTO.getEmail(), "password", "");
        mockUser.setUserId(userId);

        when(userService.getUserProfile(anyLong())).thenReturn(mockUserDTO);

        mockMvc.perform(get(USER_API_BASE_URL + "/{userId}", userId)
                .with(user("user").roles("ADMI"))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("id").value(mockUserDTO.getId()))
            .andExpect(jsonPath("name").value(mockUserDTO.getName()))
            .andExpect(jsonPath("email").value(mockUserDTO.getEmail()));
    }

    @Test
    void shouldReturnAccessToken_WhenRefreshTokenValid() throws Exception {
        String refreshToken = "refresh-token";
        var accessTokenDto = new AccessTokenDTO(
            "new-access-token",
            "new-refresh-token"
        );
        given(userService.refreshAccessToken(refreshToken)).willReturn(accessTokenDto);

        mockMvc.perform(post(USER_API_BASE_URL + "/refresh-token/{refreshToken}", refreshToken)
                .with(user("user").roles("USER"))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("accessToken").value(accessTokenDto.accessToken()))
            .andExpect(jsonPath("refreshToken").value(accessTokenDto.refreshToken()));
    }

    @Test
    void shouldReturnOkWithMessage_WhenPasswordIsReset() throws Exception {
        var request = new PasswordResetRequest("test@gmail.com", "123456", "123456");
        mockMvc.perform(patch(USER_API_BASE_URL + "/reset-password")
                .header(CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .header(ACCEPT, APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsBytes(request)))
            .andExpect(status().isOk())
            .andExpect(content().string("password is updated!"));
    }
}