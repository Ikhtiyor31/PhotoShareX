package com.ikhtiyor.photosharex.photo.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.ikhtiyor.photosharex.exception.ResourceNotFoundException;
import com.ikhtiyor.photosharex.photo.dto.PhotoRequest;
import com.ikhtiyor.photosharex.photo.dto.PhotoUpdateRequest;
import com.ikhtiyor.photosharex.photo.enums.VisibilityType;
import com.ikhtiyor.photosharex.photo.model.Photo;
import com.ikhtiyor.photosharex.user.dto.UserRegisterRequest;
import com.ikhtiyor.photosharex.user.model.User;
import com.ikhtiyor.photosharex.user.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@DataJpaTest
class PhotoRepositoryTest {


    @Autowired
    private PhotoRepository photoRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        photoRepository.deleteAll();
    }

    @Test
    void givenPhotoEntity_whenSaved_thenSuccess() {
        UserRegisterRequest request = new UserRegisterRequest(
            "abdul",
            "test@gmail.com",
            "aslfasjffasfd",
            ""
        );

        User user = User.createOf(request, "encoasdfafalas");
        User savedUser = userRepository.save(user);
        PhotoRequest photoRequest = new PhotoRequest(
            "http://localhost:8080/image_2024_02_11_23423.jpg",
            "New Image",
            "This is a beautiful image",
            VisibilityType.PUBLIC,
            "Seoul"
        );
        Photo photo = Photo.createOf(photoRequest, savedUser);
        Photo savedPhoto = photoRepository.save(photo);
        assertThat(savedPhoto.getVisibilityType()).isEqualTo(VisibilityType.PUBLIC);
        assertThat(savedPhoto.getUser().getEmail()).isEqualTo("test@gmail.com");
    }

    @Test
    void givenPhotoEntity_whenUpdatedPhoto_thenSuccess() {
        UserRegisterRequest registerRequest = new UserRegisterRequest(
            "abdul",
            "test@gmail.com",
            "aslfasjffasfd",
            ""
        );

        User user = User.createOf(registerRequest, "encoasdfafalas");
        User savedUser = userRepository.save(user);
        PhotoRequest photoRequest = new PhotoRequest(
            "http://localhost:8080/image_2024_02_11_23423.jpg",
            "My old image",
            "This is a beautiful old image",
            VisibilityType.PUBLIC,
            "Seoul"
        );
        Photo photo = Photo.createOf(photoRequest, savedUser);
        Photo savedPhoto = photoRepository.save(photo);

        Photo fetchedPhoto = photoRepository.findById(savedPhoto.getId()).orElseThrow(
            () -> new ResourceNotFoundException("Photo not found with id: " + savedPhoto.getId()));

        PhotoUpdateRequest photoUpdateRequest = new PhotoUpdateRequest(
            "My new image",
            "This is a beautiful new image",
            "Seongnam-si"
        );

        fetchedPhoto.setTitle(photoUpdateRequest.title());
        fetchedPhoto.setDescription(photoUpdateRequest.description());
        fetchedPhoto.setLocation(photoUpdateRequest.location());

        // Then
        assertThat(fetchedPhoto.getLocation()).isEqualTo(photoUpdateRequest.location());
        assertThat(fetchedPhoto.getLocation()).isNotEqualTo(photoRequest.location());
        assertThat(fetchedPhoto.getTitle()).isNotEqualTo(photoRequest.title());

    }

    @Test
    void givenPhotoEntity_whenChangePhotoVisibilityType_thenChangedToPrivate() {
        UserRegisterRequest registerRequest = new UserRegisterRequest(
            "abdul",
            "test@gmail.com",
            "aslfasjffasfd",
            ""
        );

        User user = User.createOf(registerRequest, "encoasdfafalas");
        User savedUser = userRepository.save(user);
        PhotoRequest photoRequest = new PhotoRequest(
            "http://localhost:8080/image_2024_02_11_23423.jpg",
            "My old image",
            "This is a beautiful old image",
            VisibilityType.PUBLIC,
            "Seoul"
        );
        Photo photo = Photo.createOf(photoRequest, savedUser);
        Photo savedPhoto = photoRepository.save(photo);

        Photo fetchedPhoto = photoRepository.findById(savedPhoto.getId()).get();

        // When
        fetchedPhoto.setVisibilityType(VisibilityType.PRIVATE);

        // Then
        assertThat(fetchedPhoto.getVisibilityType()).isEqualTo(VisibilityType.PRIVATE);
    }

    @Test
    void givenPhotoEntity_whenFindByUser_ReturnsPageOfPhotos() {
        // Given
        UserRegisterRequest registerRequest = new UserRegisterRequest(
            "abdul",
            "test@gmail.com",
            "aslfasjffasfd",
            ""
        );

        User user = User.createOf(registerRequest, "encoasdfafalas");
        User savedUser = userRepository.save(user);

        PhotoRequest photoRequest = new PhotoRequest(
            "http://localhost:8080/image_2024_02_11_23423.jpg",
            "My old image",
            "This is a beautiful old image",
            VisibilityType.PUBLIC,
            "Seoul"
        );
        Photo photo1 = Photo.createOf(photoRequest, savedUser);
        Photo photo2 = Photo.createOf(photoRequest, savedUser);
        photoRepository.save(photo1);
        photoRepository.save(photo2);

        PageRequest pageable = PageRequest.of(0, 10, Sort.Direction.DESC, "createdAt");

        // When
        Page<Photo> photos = photoRepository.findByUser(savedUser, pageable);

        // Then
        assertThat(photos.getContent()).isNotNull();
        assertEquals(2, photos.getTotalElements());
    }

    @Test
    void shouldCheck_whenFindByUser_ReturnsPageOfOnlyMyPhotos() {
        // Given
        UserRegisterRequest registerRequest1 = new UserRegisterRequest(
            "abdul",
            "test@gmail.com",
            "aslfasjffasfd",
            "http://localhost:8080/my-profile-image.jpg"
        );

        User user1 = User.createOf(registerRequest1, "encoasdfafalas");
        User savedUser1 = userRepository.save(user1);

        UserRegisterRequest registerRequest2 = new UserRegisterRequest(
            "abdul",
            "test2@gmail.com",
            "aslfasjffasfd",
            "http://localhost:8080/my-second-profile-image.jpg"
        );

        User user2 = User.createOf(registerRequest2, "encoasdfafalas");
        User savedUser2 = userRepository.save(user2);

        PhotoRequest photoRequest = new PhotoRequest(
            "http://localhost:8080/image_2024_02_11_23423.jpg",
            "My old image",
            "This is a beautiful old image",
            VisibilityType.PUBLIC,
            "Seoul"
        );
        Photo photo1 = Photo.createOf(photoRequest, savedUser1);
        Photo photo2 = Photo.createOf(photoRequest, savedUser2);
        photoRepository.save(photo1);
        photoRepository.save(photo2);

        PageRequest pageable = PageRequest.of(0, 10, Sort.Direction.DESC, "createdAt");

        // When
        Page<Photo> photos = photoRepository.findByUser(savedUser1, pageable);
        Page<Photo> photos2 = photoRepository.findByUser(savedUser2, pageable);

        // Then
        assertThat(photos.getContent()).isNotNull();
        assertEquals(1, photos.getTotalElements());
        assertThat(photos2.getTotalElements()).isEqualTo(1);
        assertThat(photos2.getContent()).isNotNull();
    }

    @Test
    void givenPhotoEntity_whenFindByUser_ReturnsEmptyPageOfPhotos_ifPhotoNotExist() {
        // Given
        UserRegisterRequest registerRequest = new UserRegisterRequest(
            "abdul",
            "test@gmail.com",
            "aslfasjffasfd",
            ""
        );

        User user = User.createOf(registerRequest, "encoasdfafalas");
        User savedUser = userRepository.save(user);

        PhotoRequest photoRequest = new PhotoRequest(
            "http://localhost:8080/image_2024_02_11_23423.jpg",
            "My old image",
            "This is a beautiful old image",
            VisibilityType.PUBLIC,
            "Seoul"
        );
        Photo photo1 = Photo.createOf(photoRequest, savedUser);
        Photo photo2 = Photo.createOf(photoRequest, savedUser);
        photoRepository.save(photo1);
        photoRepository.save(photo2);
        photo1.setDeleted();
        photo2.setDeleted();
        PageRequest pageable = PageRequest.of(0, 10, Sort.Direction.DESC, "createdAt");

        // When
        Page<Photo> photos = photoRepository.findByUser(savedUser, pageable);

        // Then
        assertThat(photos.getTotalElements()).isEqualTo(0);
        assertThat(photos.getContent().size()).isEqualTo(0);
    }

    @Test
    void givenPhotoEntity_whenFindById_ReturnsPhoto() {
        // Given
        UserRegisterRequest registerRequest = new UserRegisterRequest(
            "abdul",
            "test@gmail.com",
            "aslfasjffasfd",
            "\"http://localhost:8080/my-profile-image.jpg"
        );

        User user = User.createOf(registerRequest, "encoasdfafalas");
        User savedUser = userRepository.save(user);

        PhotoRequest photoRequest = new PhotoRequest(
            "http://localhost:8080/image_2024_02_11_23423.jpg",
            "My old image",
            "This is a beautiful old image",
            VisibilityType.PUBLIC,
            "Seoul"
        );
        Photo photo1 = Photo.createOf(photoRequest, savedUser);
        Photo photo2 = Photo.createOf(photoRequest, savedUser);
        photoRepository.save(photo1);
        photoRepository.save(photo2);

        // When
        Photo photo = photoRepository.findById(photo1.getId()).get();

        // Then
        assertThat(photo.getId()).isGreaterThan(0);
        assertThat(photo).isNotNull();
    }

    @Test
    void givenPhotoEntity_whenFindById_ReturnsEmpty_ifPhotoNotExist() {
        // Given
        UserRegisterRequest registerRequest = new UserRegisterRequest(
            "abdul",
            "test@gmail.com",
            "aslfasjffasfd",
            "\"http://localhost:8080/my-profile-image.jpg"
        );

        User user = User.createOf(registerRequest, "encoasdfafalas");
        User savedUser = userRepository.save(user);

        PhotoRequest photoRequest = new PhotoRequest(
            "http://localhost:8080/image_2024_02_11_23423.jpg",
            "My old image",
            "This is a beautiful old image",
            VisibilityType.PUBLIC,
            "Seoul"
        );
        Photo photo = Photo.createOf(photoRequest, savedUser);
        photoRepository.save(photo);
        photoRepository.delete(photo);
        // When
        Optional<Photo> fetchPhoto = photoRepository.findById(photo.getId());

        // Then
        assertThat(fetchPhoto).isEmpty();
    }
}