package com.ikhtiyor.photosharex.user.service;


import com.ikhtiyor.photosharex.exception.InvalidAccessTokenException;
import com.ikhtiyor.photosharex.exception.UserAlreadyExistsException;
import com.ikhtiyor.photosharex.user.dto.AccessTokenDTO;
import com.ikhtiyor.photosharex.user.dto.Token;
import com.ikhtiyor.photosharex.user.dto.UserLoginRequest;
import com.ikhtiyor.photosharex.user.dto.UserRegisterRequest;
import com.ikhtiyor.photosharex.user.dto.UserDTO;
import com.ikhtiyor.photosharex.user.dto.PasswordResetRequest;
import com.ikhtiyor.photosharex.user.model.User;
import com.ikhtiyor.photosharex.user.repository.UserRepository;
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

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder,
        AccessTokenService accessTokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.accessTokenService = accessTokenService;
    }

    @Override
    public UserDTO createUser(UserRegisterRequest request) {
        userRepository.findUserByEmail(request.email()).ifPresent(user -> {
            throw new UserAlreadyExistsException(
                "User with email " + request.email() + " already exists");
        });
        String encodedPassword = passwordEncoder.encode(request.password());
        User createdUser = userRepository.save(User.createOf(request, encodedPassword));

        return new UserDTO(createdUser.getId(), createdUser.getName(), createdUser.getEmail());
    }

    @Override
    public AccessTokenDTO authenticateUser(UserLoginRequest request) {
        User user = userRepository.findUserByEmail(request.email())
            .orElseThrow(() -> new UsernameNotFoundException(
                "User not found with email: " + request.email()));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new UsernameNotFoundException("email or password is wrong!");
        }
        Token tokens = accessTokenService.createAccessToken(user.getEmail());

        return new AccessTokenDTO(tokens.getAccess(), tokens.getRefresh());
    }

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
            return new AccessTokenDTO(tokens.getAccess(), tokens.getRefresh());
        }

        throw new InvalidAccessTokenException("Invalid or expired refresh token");
    }

    @Override
    public void resetPassword(PasswordResetRequest request) {
        var user = userRepository.findUserByEmail(request.email()).orElseThrow(
            () -> new UsernameNotFoundException("User not found with userId: " + request.email()));

        if (!passwordEncoder.matches(request.oldPassword(), user.getPassword())) {
            throw new UsernameNotFoundException("email or password is wrong!");
        }

        String encodePassword = passwordEncoder.encode(request.newPassword());
        user.updatePassword(encodePassword);
    }
}
