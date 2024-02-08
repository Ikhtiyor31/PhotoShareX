package com.ikhtiyor.photosharex.user.service;

import com.ikhtiyor.photosharex.user.dto.AccessTokenDTO;
import com.ikhtiyor.photosharex.user.dto.UserLoginRequest;
import com.ikhtiyor.photosharex.user.dto.UserResgisterRequest;
import com.ikhtiyor.photosharex.user.dto.UserDTO;
import com.ikhtiyor.photosharex.user.dto.PasswordResetRequest;

public interface UserService {

    UserDTO createUser(UserResgisterRequest request);

    AccessTokenDTO authenticateUser(UserLoginRequest request);

    UserDTO getUserProfile(Long userId);

    AccessTokenDTO refreshAccessToken(String refreshToken);

    void resetPassword(PasswordResetRequest request);
}
