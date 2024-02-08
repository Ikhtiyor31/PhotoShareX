package com.ikhtiyor.photosharex.security;

import com.ikhtiyor.photosharex.user.repository.UserRepository;
import com.ikhtiyor.photosharex.user.service.AccessTokenService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AccessTokenAuthenticationProvider implements AuthenticationProvider {

    private final AccessTokenService accessTokenService;
    private final UserRepository userRepository;


    public AccessTokenAuthenticationProvider(AccessTokenService accessTokenService,
        UserRepository userRepository) {
        this.accessTokenService = accessTokenService;
        this.userRepository = userRepository;
    }

    @Transactional(noRollbackFor = BadCredentialsException.class)
    @Override
    public Authentication authenticate(Authentication authentication)
        throws AuthenticationException {
        var token = authentication.getCredentials().toString();
        if (!accessTokenService.isValidToken(token)) {
            throw new BadCredentialsException("Invalid access token");
        }

        var username = accessTokenService.extractUserEmail(token);
        var user = userRepository
            .findUserByEmail(username)
            .orElseThrow(() -> new BadCredentialsException("Invalid access token"));

        UserAdapter userAdapter = new UserAdapter(user);
        authentication.setAuthenticated(true);
        return new UsernamePasswordAuthenticationToken(userAdapter, null,
            userAdapter.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return AccessTokenAuthentication.class.equals(authentication);
    }
}
