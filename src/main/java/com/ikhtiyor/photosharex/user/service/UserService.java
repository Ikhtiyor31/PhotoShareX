package com.ikhtiyor.photosharex.user.service;

import com.ikhtiyor.photosharex.user.dto.AccessTokenDTO;
import com.ikhtiyor.photosharex.user.dto.PasswordResetRequest;
import com.ikhtiyor.photosharex.user.dto.UserDTO;
import com.ikhtiyor.photosharex.user.dto.UserLoginRequest;
import com.ikhtiyor.photosharex.user.dto.UserRegisterRequest;
import com.ikhtiyor.photosharex.user.dto.VerificationCodeRequest;

public interface UserService {

    UserDTO createUser(UserRegisterRequest request);

    AccessTokenDTO authenticateUser(UserLoginRequest request);

    void verifyEmail(VerificationCodeRequest request);

    UserDTO getUserProfile(Long userId);

    AccessTokenDTO refreshAccessToken(String refreshToken);

    void resetPassword(PasswordResetRequest request);

    void deleteUser(Long id);
}
