package com.ikhtiyor.photosharex.user.repository;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.ikhtiyor.photosharex.user.dto.UserRegisterRequest;
import com.ikhtiyor.photosharex.user.model.User;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest
@Transactional
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;


    @BeforeEach
    @Transactional
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void givenUserEntity_whenSave_theSuccess() {
        // Givne
        UserRegisterRequest request = new UserRegisterRequest(
            "abdul",
            "test@gmail.com",
            "aslfasjffasfd",
            ""
        );

        User user = User.createOf(request, "myencodedpassword");

        // When
        User savedUser = userRepository.save(user);

        // Then
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo(request.email());
    }

    @Test
    void givenUserEntity_whenSaved_thenFindByIdReturnsUser() {
        // Given
        UserRegisterRequest request = new UserRegisterRequest(
            "test",
            "test@gmail.com",
            "password",
            ""
        );

        User user = User.createOf(request, "myencodedpassword");

        // When
        User savedUser = userRepository.save(user);

        Optional<User> foundUserOptional = userRepository.findById(savedUser.getId());
        assertTrue(foundUserOptional.isPresent());
        assertThat(foundUserOptional.get()).isEqualTo(savedUser);
    }

    @Test
    void givenExistingUserEntity_whenDeleted_thenNotFound() {
        // Given
        UserRegisterRequest request = new UserRegisterRequest(
            "test",
            "test@gmail.com",
            "password",
            ""
        );

        User user = User.createOf(request, "myencodedpassword");
        User savedUser = userRepository.save(user);

        // When
        userRepository.delete(savedUser);

        // Then
        Optional<User> foundUserOptional = userRepository.findById(savedUser.getId());
        assertThat(foundUserOptional).isEmpty();
    }

    @Test
    void givenUserEntity_whenFindByEmail_thenSuccess() {
        // Given
        UserRegisterRequest request = new UserRegisterRequest(
            "test",
            "another@gmail.com",
            "aslfasjffasfd",
            "http://localhost:8080/my-profile-image.jpg"
        );

        User user = User.createOf(request, "asd2asfsdf2323sdasfdasfd");
        // When
        userRepository.save(user);

        // Then
        User foundUser = userRepository.findUserByEmail(request.email())
            .orElseThrow(()-> new UsernameNotFoundException("user not found"));
        assertThat(foundUser).isNotNull();
        assertThat(user.getName()).isEqualTo(request.name());
        assertThat(user.getEmail()).isEqualTo(request.email());
    }

    @Test
    void givenUserEntity_whenNotFindByEmail_thenFail() {
        // Given
        UserRegisterRequest request = new UserRegisterRequest(
            "tourist",
            "tourist@gmail.com",
            "aslfasjffasfdasfas",
            "http://localhost:8080/my-personal-image.jpg"
        );

        User user = User.createOf(request, "asfdadsfgwegkiweuir99234");
        final var savedUser = userRepository.save(user);

        // When
        userRepository.delete(savedUser);

        //Then
        Optional<User> userOptional = userRepository.findUserByEmail("different@gmail.com");
        assertThat(userOptional).isEqualTo(Optional.empty());
    }

    @Test
    void givenUserEntity_whenUserSetDeleted_thenReturnEmpty() {
        // Given
        UserRegisterRequest request = new UserRegisterRequest(
            "test",
            "test@gmail.com",
            "password",
            ""
        );

        User user = User.createOf(request, "myencodedpassword");
        User savedUser = userRepository.save(user);

        // When
        savedUser.setDeleted();

        // Then
        Optional<User> foundUserOptional = userRepository.findUserByEmail(savedUser.getEmail());
        assertThat(foundUserOptional).isEmpty();
    }

}