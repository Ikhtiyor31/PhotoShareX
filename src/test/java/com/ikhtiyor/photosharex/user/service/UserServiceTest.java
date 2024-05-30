package com.ikhtiyor.photosharex.user.service;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

import com.ikhtiyor.photosharex.exception.InvalidAccessTokenException;
import com.ikhtiyor.photosharex.exception.ResourceNotFoundException;
import com.ikhtiyor.photosharex.exception.UserAlreadyExistsException;
import com.ikhtiyor.photosharex.user.dto.PasswordResetRequest;
import com.ikhtiyor.photosharex.user.dto.RegistrationCompleteEvent;
import com.ikhtiyor.photosharex.user.dto.Token;
import com.ikhtiyor.photosharex.user.dto.UserDTO;
import com.ikhtiyor.photosharex.user.dto.UserLoginRequest;
import com.ikhtiyor.photosharex.user.dto.UserRegisterRequest;
import com.ikhtiyor.photosharex.user.dto.VerificationCodeRequest;
import com.ikhtiyor.photosharex.user.model.User;
import com.ikhtiyor.photosharex.user.model.VerificationCode;
import com.ikhtiyor.photosharex.user.repository.UserRepository;
import com.ikhtiyor.photosharex.user.repository.VerificationCodeRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AccessTokenServiceImpl accessTokenService;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private VerificationCodeRepository verificationCodeRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void shouldCreateUser_whenUserNotExists() {
        // Given
        UserRegisterRequest request = new UserRegisterRequest(
            "ikhtiyor",
            "new@example.com",
            "password",
            "http://localhost:8080/my-profile-photo.jpg"
        );
        User newUser = User.createOf(request, "asflasljkdfalskjf");

        // Mocking
        when(userRepository.findUserByEmail(request.email())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(request.password())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(newUser);
        eventPublisher.publishEvent(new RegistrationCompleteEvent(request.name(), request.email()));

        // When
        var userDto = userService.createUser(request);

        // Then
        assertNotNull(userDto);
        assertFalse(userDto.getEmail().isEmpty());
        assertThat(userDto.getEmail()).isEqualTo(request.email());
        assertThat(userDto.getName()).isEqualTo(request.name());
    }

    @Test
    void shouldFailToSaveUserEntity_WhenUserAlreadyExists() {
        // Given
        UserRegisterRequest request = new UserRegisterRequest(
            "fake",
            "new@example.com",
            "password",
            "http://localhost:8080/my-profile-photo.jpg"
        );

        var newUser = User.createOf(request, "asflasljkdfalskjf");
        // When
        when(userRepository.findUserByEmail(request.email())).thenReturn(Optional.of(newUser));
        // Then
        assertThrows(UserAlreadyExistsException.class, () -> userService.createUser(request));
    }

    @Test
    void shouldAuthenticateUser() {
        // Given
        String email = "fake@example.com";
        String password = "password1235";
        var registerRequest = new UserRegisterRequest(
            "fake",
            email,
            password,
            ""
        );
        var loginRequest = new UserLoginRequest(email, password);
        var user = User.createOf(registerRequest, password);
        user.setEnabled(true);
        // When
        Mockito.when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(user));
        Mockito.when(passwordEncoder.matches(loginRequest.password(), user.getPassword()))
            .thenReturn(true);
        Mockito.when(accessTokenService.createAccessToken(user.getEmail()))
            .thenReturn(Token.of("access-token", "refresh-token"));
        var accessTokenDto = userService.authenticateUser(loginRequest);

        // Then
        assertNotNull(accessTokenDto.accessToken());
        assertNotNull(accessTokenDto.refreshToken());
        assertFalse(accessTokenDto.accessToken().isEmpty());
        assertFalse(accessTokenDto.refreshToken().isEmpty());
    }

    @Test
    void shouldThrowUsernameNotFoundException_WhenUserNotEnabled() {
        // Given
        var registerRequest = new UserRegisterRequest("fakeUser", "fake@gmail.com", "pass", "sasf");
        var loginRequest = new UserLoginRequest("fake@gmail.com", "pass");
        var user = User.createOf(registerRequest, "pass");

        // When
        Mockito.when(userRepository.findUserByEmail(loginRequest.email()))
            .thenReturn(Optional.of(user));

        // Then
        assertThrows(UsernameNotFoundException.class,
            () -> userService.authenticateUser(loginRequest));
        assertFalse(user.isEnabled());

    }

    @Test
    void shouldThrowException_WhenPasswordMismatch() {
        // Given
        var registerRequest = new UserRegisterRequest("fakeUser", "fake@gmail.com", "pass", "");
        var loginRequest = new UserLoginRequest("fake@gmail.com", "pass");
        var user = User.createOf(registerRequest, "encodedpasdf");
        user.setEnabled(true);
        // When
        Mockito.when(userRepository.findUserByEmail(loginRequest.email()))
            .thenReturn(Optional.of(user));

        // Then
        assertThrows(UsernameNotFoundException.class,
            () -> userService.authenticateUser(loginRequest));
        assertTrue(user.isEnabled());
    }

    @Test
    void shouldVerifyEmail_whenEmailVerificationCode_isValid() {
        // Given
        String email = "example@gmail.com";
        String verificationCode = "1234";
        var request = new VerificationCodeRequest(email, verificationCode);
        var fetchVerification = new VerificationCode(verificationCode,
            LocalDateTime.now().plusMinutes(5), email);

        var user = new User();
        // When
        given(verificationCodeRepository.findTopByEmailOrderByIdDesc(email)).willReturn(
            Optional.of(fetchVerification));
        given(userRepository.findUserByEmail(request.email())).willReturn(Optional.of(user));

        userService.verifyEmail(request);

        assertTrue(user.isEnabled());
        assertThat(fetchVerification.getVerificationTime()).isNotNull();
    }

    @Test
    void shouldThrowVerificationCodeException_WhenCodeHasExpired() {
        // Given
        String email = "example@gmail.com";
        String verificationCode = "1234";
        var request = new VerificationCodeRequest(email, verificationCode);
        var fetchVerification = new VerificationCode(verificationCode,
            LocalDateTime.now().minusMinutes(1), email);
        // When
        given(verificationCodeRepository.findTopByEmailOrderByIdDesc(email)).willReturn(
            Optional.of(fetchVerification));
        // Then
        assertThrows(IllegalArgumentException.class, () -> userService.verifyEmail(request));
        assertTrue(fetchVerification.isExpired());
    }

    @Test
    void shouldThrowException_whenEmailAlreadyVerified() {
        String email = "example@gmail.com";
        String verificationCode = "1234";
        var request = new VerificationCodeRequest(email, verificationCode);
        var fetchVerification = new VerificationCode(verificationCode,
            LocalDateTime.now().plusMinutes(5), email);
        fetchVerification.setVerificationTime(LocalDateTime.now());
        // When
        given(verificationCodeRepository.findTopByEmailOrderByIdDesc(email)).willReturn(
            Optional.of(fetchVerification));
        // Then
        assertThrows(IllegalArgumentException.class, () -> userService.verifyEmail(request));
        assertThat(fetchVerification.getVerificationTime()).isNotNull();
    }

    @Test
    void shouldThrowException_whenRequestVerificationCodeMisMatch() {
        String email = "example@gmail.com";
        String verificationCode = "1234";
        String differentVerificationCode = "4321";
        var request = new VerificationCodeRequest(email, verificationCode);
        var fetchVerification = new VerificationCode(
            differentVerificationCode,
            LocalDateTime.now().plusMinutes(5),
            email
        );
        // When
        given(verificationCodeRepository.findTopByEmailOrderByIdDesc(email))
            .willReturn(Optional.of(fetchVerification));
        // Then
        assertThrows(IllegalArgumentException.class, () -> userService.verifyEmail(request));
        assertThat(fetchVerification.getCode()).isNotEqualTo(request.verificationCode());
    }

    @Test
    void shouldThrowException_WhenVerificationCodeNotFoundByEmail() {
        // Given
        var request = new VerificationCodeRequest("test@gmail.com", "1235");

        // When
        given(verificationCodeRepository.findTopByEmailOrderByIdDesc("test@gmail.com")).willReturn(Optional.empty());

        // Then
        assertThatThrownBy(() -> userService.verifyEmail(request))
            .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("verification code not found with email: " + request.email());
    }

    @Test
    void getUserProfile() {
    }

    @Test
    void shouldGenerateNewAccessToken_whenRefreshTokenValid() {
        // Given
        String fakeRefreshToken = "eyasdfkjhklhAkljhasfljh24lhkjsdf.kjasdf";
        String email = "fake@gmail.com";
        // When
        Mockito.when(accessTokenService.isValidToken(fakeRefreshToken)).thenReturn(true);
        Mockito.when(accessTokenService.extractUserEmail(fakeRefreshToken)).thenReturn(email);
        Mockito.when(accessTokenService.createAccessToken(email))
            .thenReturn(new Token("access-token", "refresh-token"));
        var accessTokenDto = userService.refreshAccessToken(fakeRefreshToken);
        // Then
        assertThat(accessTokenDto.accessToken()).isNotNull();
        assertThat(accessTokenDto.refreshToken()).isNotNull();
    }

    @Test
    void shouldThrowException_whenRefreshTokenInvalid() {
        // Given
        String fakeRefreshToken = "lkjhkllKJHKjhhlkL234";
        // When
        Mockito.when(accessTokenService.isValidToken(fakeRefreshToken)).thenReturn(false);
        // Then
        assertThrows(InvalidAccessTokenException.class,
            () -> userService.refreshAccessToken(fakeRefreshToken));
    }

    @Test
    void shouldResetPassword_WhenProvidedOldPassword_IsValid() {
        // Given
        String oldPassword = "oldPassword";
        String newPassword = "newPassword";
        var request = new PasswordResetRequest("test@gmail.com", oldPassword, newPassword);
        var userRequest = new UserRegisterRequest("", "", oldPassword, "");
        var user = User.createOf(userRequest, oldPassword);
        user.setEnabled(true);
        // When
        given(userRepository.findUserByEmail(request.email())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(request.oldPassword(), user.getPassword())).willReturn(true);
        given(passwordEncoder.encode(newPassword)).willReturn(newPassword);
        userService.resetPassword(request);
        // Then
        assertThat(user.getPassword()).isNotEqualTo(oldPassword);
        assertThat(user.getPassword()).isEqualTo(newPassword);
    }

    @Test
    void shouldThrowException_WhenOldPasswordAndNewPasswordMismatch() {
        // Given
        String oldPassword = "oldPassword";
        String newPassword = "newPassword";
        var request = new PasswordResetRequest("test@gmail.com", oldPassword, newPassword);
        var userRequest = new UserRegisterRequest("", "", oldPassword, "");
        var user = User.createOf(userRequest, oldPassword);
        user.setEnabled(true);
        // When
        given(userRepository.findUserByEmail(request.email())).willReturn(Optional.of(user));
        assertThatThrownBy(() -> userService.resetPassword(request))
            .isInstanceOf(UsernameNotFoundException.class)
            .hasMessage("email or password is wrong!");
    }

    @Test
    public void testGetUserProfile_UserExists() {
        // Given
        UserRegisterRequest request = new UserRegisterRequest(
            "ikhtiyor",
            "new@example.com",
            "password",
            "http://localhost:8080/my-profile-photo.jpg"
        );
        var user = User.createOf(request, "asflasljkdfalskjf");
        user.setUserId(1L);
        // Arrange

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        // Act
        UserDTO userDTO = userService.getUserProfile(1L);

        // Then
        assertNotNull(userDTO);
        assertThat(1L).isEqualTo(userDTO.getId());
        assertThat(request.name()).isEqualTo(userDTO.getName());
        assertThat(request.email()).isEqualTo(userDTO.getEmail());
    }



    @Test
    public void testGetUserProfile_UserNotFound() {
        // Given
        Long userId = 1L;

        // Arrange
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
       assertThatThrownBy(() -> userService.getUserProfile(userId))
           .isInstanceOf(UsernameNotFoundException.class)
           .hasMessage("User not found with userId: " + userId);
    }
}