package com.ikhtiyor.photosharex.photo.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.ikhtiyor.photosharex.photo.dto.PhotoRequest;
import com.ikhtiyor.photosharex.photo.enums.VisibilityType;
import com.ikhtiyor.photosharex.photo.model.Photo;
import com.ikhtiyor.photosharex.user.dto.UserRegisterRequest;
import com.ikhtiyor.photosharex.user.model.User;
import com.ikhtiyor.photosharex.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class PhotoRepositoryTest {


    @Autowired
    private PhotoRepository photoRepository;

    @Autowired
    private UserRepository userRepository;

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
            "This is a beatiful image",
            VisibilityType.PUBLIC,
            "Seoul"
        );
        Photo photo = Photo.createOf(photoRequest, savedUser);
        Photo savedPhoto = photoRepository.save(photo);
        assertThat(savedPhoto.getVisibilityType()).isEqualTo(VisibilityType.PUBLIC);
        assertThat(savedPhoto.getUser().getEmail()).isEqualTo("test@gmail.com");
    }

}