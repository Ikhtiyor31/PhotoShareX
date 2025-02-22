package com.ikhtiyor.photosharex.user.service;


import com.ikhtiyor.photosharex.exception.InvalidAccessTokenException;
import com.ikhtiyor.photosharex.exception.ResourceNotFoundException;
import com.ikhtiyor.photosharex.exception.UserAlreadyExistsException;
import com.ikhtiyor.photosharex.user.dto.AccessTokenDTO;
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
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccessTokenService accessTokenService;
    private final ApplicationEventPublisher eventPublisher;
    private final VerificationCodeRepository verificationCodeRepository;

    public UserServiceImpl(
        UserRepository userRepository,
        PasswordEncoder passwordEncoder,
        AccessTokenService accessTokenService,
        ApplicationEventPublisher eventPublisher,
        VerificationCodeRepository verificationCodeRepository
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.accessTokenService = accessTokenService;
        this.eventPublisher = eventPublisher;
        this.verificationCodeRepository = verificationCodeRepository;
    }

    @Override
    public UserDTO createUser(UserRegisterRequest request) {
        userRepository.findUserByEmail(request.email()).ifPresent(user -> {
            throw new UserAlreadyExistsException(
                "User with email " + request.email() + " already exists");
        });

        String encodedPassword = passwordEncoder.encode(request.password());
        User createdUser = userRepository.save(User.createOf(request, encodedPassword));
        eventPublisher.publishEvent(new RegistrationCompleteEvent(
            createdUser.getName(),
            createdUser.getEmail()
        ));

        return new UserDTO(createdUser.getId(), createdUser.getName(), createdUser.getEmail());
    }

    @Override
    public AccessTokenDTO authenticateUser(UserLoginRequest request) {
        User user = getUser(request.email());

        if (!user.isEnabled() || !passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new UsernameNotFoundException("email or password is wrong!");
        }

        Token tokens = accessTokenService.createAccessToken(user.getEmail());

        return new AccessTokenDTO(tokens.access(), tokens.refresh());
    }

    @Override
    public void verifyEmail(VerificationCodeRequest request) {
        VerificationCode verificationCode = verificationCodeRepository
            .findTopByEmailOrderByIdDesc(request.email())
            .orElseThrow(() -> new ResourceNotFoundException("verification code not found with email: " + request.email()));

        if (verificationCode.isExpired()) {
            throw new IllegalArgumentException("Verification code has expired!");
        }

        if (verificationCode.getVerificationTime() != null) {
            throw new IllegalArgumentException("Your email address has been already verified");
        }

        if (!verificationCode.getCode().equals(request.verificationCode())) {
            throw new IllegalArgumentException("Verification code is wrong");
        }
        var user = getUser(request.email());
        user.setEnabled(true);
        verificationCode.setVerificationTime(LocalDateTime.now());
    }

    @Transactional(readOnly = true)
    @Override
    public UserDTO getUserProfile(Long userId) {
        var user = userRepository.findById(userId).orElseThrow(
            () -> new UsernameNotFoundException("User not found with userId: " + userId));

        return new UserDTO(user.getId(), user.getName(), user.getEmail());
    }

    @Override
    public AccessTokenDTO refreshAccessToken(String refreshToken) {
        boolean isValidToken = accessTokenService.isValidToken(refreshToken);
        if (isValidToken) {
            var email = accessTokenService.extractUserEmail(refreshToken);
            Token tokens = accessTokenService.createAccessToken(email);
            return new AccessTokenDTO(tokens.access(), tokens.refresh());
        }

        throw new InvalidAccessTokenException("Invalid or expired refresh token");
    }

    @Override
    public void resetPassword(PasswordResetRequest request) {
        var user = getUser(request.email());

        if (!user.isEnabled() || !passwordEncoder.matches(request.oldPassword(), user.getPassword())) {
            throw new UsernameNotFoundException("email or password is wrong!");
        }

        String encodePassword = passwordEncoder.encode(request.newPassword());
        user.updatePassword(encodePassword);
    }

    @Override
    public void deleteUser(Long userId) {
        var user = userRepository.findById(userId)
            .orElseThrow(
                () -> new UsernameNotFoundException("User not found with userId: " + userId));

        user.setDeleted();
    }

    private User getUser(String email) {
        return userRepository
            .findUserByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }
}
