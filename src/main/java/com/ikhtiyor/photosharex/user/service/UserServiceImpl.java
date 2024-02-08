package com.ikhtiyor.photosharex.user.service;


import com.ikhtiyor.photosharex.user.dto.AccessTokenDTO;
import com.ikhtiyor.photosharex.user.dto.UserLoginRequest;
import com.ikhtiyor.photosharex.user.dto.UserResgisterRequest;
import com.ikhtiyor.photosharex.user.dto.UserDTO;
import com.ikhtiyor.photosharex.user.model.User;
import com.ikhtiyor.photosharex.user.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
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
    public UserDTO createUser(UserResgisterRequest request) {
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        User createdUser = userRepository.save(User.createOf(request, encodedPassword));

        return new UserDTO(createdUser.getId(), createdUser.getName(), createdUser.getEmail());
    }

    @Override
    public AccessTokenDTO authenticateUser(UserLoginRequest request) {
        User user = userRepository.findUserByEmail(request.getEmail())
            .orElseThrow(() -> new UsernameNotFoundException(
                "User not found with email: " + request.getEmail()));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("email or password is wrong!");
        }

        String accessToken = accessTokenService.createAccessToken(user.getEmail());
        String refreshToken = accessTokenService.createRefreshToken(user.getEmail());

        return new AccessTokenDTO(accessToken, refreshToken);
    }

    @Override
    public UserDTO getUserProfile(Long userId) {
        var user =  userRepository.findById(userId).orElseThrow(
            () -> new UsernameNotFoundException("User not found with userId: " + userId));

        return new UserDTO(user.getId(), user.getName(), user.getEmail());
    }
}
