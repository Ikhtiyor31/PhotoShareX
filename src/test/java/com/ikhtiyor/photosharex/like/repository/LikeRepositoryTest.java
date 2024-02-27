package com.ikhtiyor.photosharex.like.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.ikhtiyor.photosharex.like.model.Like;
import com.ikhtiyor.photosharex.photo.dto.PhotoRequest;
import com.ikhtiyor.photosharex.photo.enums.VisibilityType;
import com.ikhtiyor.photosharex.photo.model.Photo;
import com.ikhtiyor.photosharex.photo.repository.PhotoRepository;
import com.ikhtiyor.photosharex.user.dto.UserRegisterRequest;
import com.ikhtiyor.photosharex.user.model.User;
import com.ikhtiyor.photosharex.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class LikeRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PhotoRepository photoRepository;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        photoRepository.deleteAll();
        likeRepository.deleteAll();
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void giveLikeEntity_whenSave_thenCreateLike() {
        // Given
        PhotoRequest photoRequest = new PhotoRequest(
            "http://localhost:8080/image_2024_02_11_23423.jpg",
            "New Image",
            "This is a beautiful image",
            VisibilityType.PUBLIC,
            "Seoul"
        );
        String email = "test1@gmail.com";
        User user = getUser(email);
        Photo photo = Photo.createOf(photoRequest, user);
        Photo savedPhoto = photoRepository.save(photo);
        Like like = Like.createOf(savedPhoto, user);

        // When
        likeRepository.save(like);

        // Then
        assertThat(like.getId()).isGreaterThan(0);
        assertThat(like.getId()).isEqualTo(1);
        assertThat(like.getUser().getEmail()).isEqualTo(email);
    }

    @Test
    void givenLikeEntity_whenLikeRemoved_thenSuccess() {
        // Given
        PhotoRequest photoRequest = new PhotoRequest(
            "http://localhost:8080/image_2024_02_11_23423.jpg",
            "New Image",
            "This is a beautiful image",
            VisibilityType.PUBLIC,
            "Seoul"
        );
        String email = "test1@gmail.com";
        User user = getUser(email);
        Photo photo = Photo.createOf(photoRequest, user);
        Photo savedPhoto = photoRepository.save(photo);
        Like like = Like.createOf(savedPhoto, user);
        Like like1 = Like.createOf(savedPhoto, user);
        Like like2 = Like.createOf(savedPhoto, user);

        // When
        likeRepository.saveAll(List.of(like, like1, like2));
        like.setDeleted();
        boolean likeExists = likeRepository.existsById(like.getId());

        // Then
        assertFalse(likeExists);
    }

    private User getUser(String email) {
        UserRegisterRequest request = new UserRegisterRequest(
            "abdul",
            email,
            "aslfasjffasfd",
            ""
        );

        User user = User.createOf(request, "encoasdfafalas");
        return userRepository.save(user);
    }
}