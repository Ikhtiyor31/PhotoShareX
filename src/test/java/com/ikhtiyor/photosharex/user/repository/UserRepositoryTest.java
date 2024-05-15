package com.ikhtiyor.photosharex.user.repository;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.ikhtiyor.photosharex.photo.dto.PhotoRequest;
import com.ikhtiyor.photosharex.photo.enums.VisibilityType;
import com.ikhtiyor.photosharex.photo.model.Photo;
import com.ikhtiyor.photosharex.photo.repository.PhotoRepository;
import com.ikhtiyor.photosharex.user.dto.UserRegisterRequest;
import com.ikhtiyor.photosharex.user.model.User;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PhotoRepository photoRepository;

    @Autowired
    private EntityManager entityManager;


    @BeforeEach
    @Transactional
    void setUp() {
        userRepository.deleteAll();
        photoRepository.deleteAll();
    }

    @Test
    void givenUserEntity_whenSave_thenSuccess() {
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

    @Test
    void givenUserEntity_whenGetPhotoByUser() {
        // Given
        UserRegisterRequest request = new UserRegisterRequest(
            "test",
            "test@gmail.com",
            "password",
            ""
        );

        User user = User.createOf(request, "myencodedpassword");
        User savedUser = userRepository.saveAndFlush(user);

        PhotoRequest photoRequest = new PhotoRequest(
            "http://localhost:8080/image_2024_02_11_23423.jpg",
            "New Image",
            "This is a beautiful image",
            VisibilityType.PUBLIC,
            "Seoul"
        );
        Photo photo1 = Photo.createOf(photoRequest, savedUser);
        Photo photo2 = Photo.createOf(photoRequest, savedUser);
        photoRepository.saveAndFlush(photo1);
        photoRepository.saveAndFlush(photo2);
        entityManager.clear();

        // When
        userRepository.deleteById(savedUser.getId());
        entityManager.flush();
        entityManager.clear();
        PageRequest pageable = PageRequest.of(0, 10, Sort.Direction.DESC, "createdAt");
        List<Photo> photos = photoRepository.findByUser(savedUser, pageable).toList();

        // Then
        assertTrue(photos.isEmpty());
    }

    @Test
    void givenUserEntity_whenRemoveOneOfPhotoOfUser() {
        // Given
        UserRegisterRequest request = new UserRegisterRequest(
            "test",
            "test@gmail.com",
            "password",
            ""
        );

        User user = User.createOf(request, "myencodedpassword");
        User savedUser = userRepository.saveAndFlush(user);

        PhotoRequest photoRequest = new PhotoRequest(
            "http://localhost:8080/image_2024_02_11_23423.jpg",
            "New Image",
            "This is a beautiful image",
            VisibilityType.PUBLIC,
            "Seoul"
        );
        Photo photo1 = Photo.createOf(photoRequest, savedUser);
        Photo photo2 = Photo.createOf(photoRequest, savedUser);
        photoRepository.saveAndFlush(photo1);
        photoRepository.saveAndFlush(photo2);
        entityManager.clear();

        User fetchUser = userRepository.findById(savedUser.getId()).get();
        fetchUser.getPhotos().remove(1);

        // When
        List<Photo> photos = photoRepository.findAll();

        // Then
        assertThat(photos.size()).isEqualTo(1);
    }

}