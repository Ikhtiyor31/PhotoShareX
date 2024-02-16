package com.ikhtiyor.photosharex.photo.repository;

import static org.assertj.core.api.Assertions.assertThat;
import com.ikhtiyor.photosharex.photo.dto.AlbumRequest;
import com.ikhtiyor.photosharex.photo.model.Album;
import com.ikhtiyor.photosharex.photo.model.SharedAlbum;
import com.ikhtiyor.photosharex.user.dto.UserRegisterRequest;
import com.ikhtiyor.photosharex.user.model.User;
import com.ikhtiyor.photosharex.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;


@DataJpaTest
class SharedAlbumRepositoryTest {

    @Autowired
    private SharedAlbumRepository sharedAlbumRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AlbumRepository albumRepository;

    @BeforeEach
    void setup() {
        sharedAlbumRepository.deleteAll();
        userRepository.deleteAll();
        albumRepository.deleteAll();
    }

    @Test
    void givenEntitySharedAlbum_whenSave_thenSaveEntity() {
        AlbumRequest albumRequest = new AlbumRequest(
            "My album",
            "this is my description about album"
        );
        String email = "test1@gmail.com";
        User user = getUser(email);
        Album album = Album.of(albumRequest, user);
        albumRepository.save(album);

        SharedAlbum sharedAlbum = SharedAlbum.createOf(album, user);
        SharedAlbum savedSharedAlbum = sharedAlbumRepository.save(sharedAlbum);
        assertThat(savedSharedAlbum.getId()).isGreaterThan(0);
        assertThat(savedSharedAlbum.getSharedUser().getEmail()).isEqualTo(email);

    }

    private User getUser(String email) {
        UserRegisterRequest registerRequest = new UserRegisterRequest(
            "abdul",
            email,
            "aslfasjffasfd",
            "\"http://localhost:8080/my-profile-image.jpg"
        );

        User user = User.createOf(registerRequest, registerRequest.password());
        return userRepository.save(user);
    }
}