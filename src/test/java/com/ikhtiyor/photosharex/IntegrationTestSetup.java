package com.ikhtiyor.photosharex;

import static org.mockito.Mockito.when;

import com.ikhtiyor.photosharex.security.UserAdapter;
import com.ikhtiyor.photosharex.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

public abstract class IntegrationTestSetup {

    protected MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;


    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        Long userId = 1L;
        String name = "Ikhtiyor";
        String email = "ikhtiyor@mail.com";
        String password = "abc123456";
        var user = new User(name, email, password, "");
        user.setUserId(userId);
        var userAdapter = new UserAdapter(user);

        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userAdapter);
    }
}
