package com.ikhtiyor.photosharex.photo.repository;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.ikhtiyor.photosharex.photo.dto.AlbumRequest;
import com.ikhtiyor.photosharex.photo.model.Album;
import com.ikhtiyor.photosharex.user.dto.UserRegisterRequest;
import com.ikhtiyor.photosharex.user.model.User;
import com.ikhtiyor.photosharex.user.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class AlbumRepositoryTest {

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        albumRepository.deleteAll();
    }

    @Test
    void givenAlbum_whenSave_thenSuccessfullySave() {
        AlbumRequest albumRequest = new AlbumRequest(
            "My new album",
            "this is my description about album"
        );
        String email = "tourist@gmail.com";
        Album album = Album.of(albumRequest, getUser(email));
        final var savedAlbum = albumRepository.save(album);
        assertThat(savedAlbum).isNotNull();
        assertThat(savedAlbum.getId()).isGreaterThan(0);
    }

    @Test
    void givenAlbum_whenUpdateAlbumCoverImage_thenUpdateSuccess() {
        AlbumRequest albumRequest = new AlbumRequest(
            "My new album",
            "this is my description about album"
        );
        String email = "test@gmail.com";
        Album album = Album.of(albumRequest, getUser(email));
        final var savedAlbum = albumRepository.save(album);
        String albumCoverImageUrl = "http://localhost:8080/my-album-cover-image.jpg";
        savedAlbum.updateCoverImage(albumCoverImageUrl);
        assertThat(savedAlbum.getCoverImageUrl()).isNotNull();
        assertThat(savedAlbum.getCoverImageUrl()).isEqualTo(albumCoverImageUrl);
    }

    @Test
    void givenAlbum_whenFindByUserAndAlbumId_thenReturnAlbum() {
        // Given
        AlbumRequest albumRequest = new AlbumRequest(
            "My new album",
            "this is my description about album"
        );
        String email = "someone@gmail.com";
        User user = getUser(email);
        Album album = Album.of(albumRequest, user);
        final var savedAlbum = albumRepository.save(album);
        final var fetchAlbum = albumRepository.findByUserAndId(user, savedAlbum.getId()).get();
        assertThat(fetchAlbum).isNotNull();
        assertThat(fetchAlbum.getId()).isEqualTo(savedAlbum.getId());
    }

    @Test
    void givenAlbum_whenFindByUserAndAlbumId_thenReturnNull_ifAlbumBelongsToDifferentUser() {
        // Given
        AlbumRequest albumRequest = new AlbumRequest(
            "My new album",
            "this is my description about album"
        );
        String email1 = "test1@gmail.com";
        String email2 = "test2@gmail.com";

        User user1 = getUser(email1);
        User user2 = getUser(email2);

        Album album = Album.of(albumRequest, user1);
        final var savedAlbum = albumRepository.save(album);
        final var fetchAlbum = albumRepository.findByUserAndId(user2, savedAlbum.getId());
        assertThat(fetchAlbum).isEqualTo(Optional.empty());
    }


    public User getUser(String email) {
        UserRegisterRequest request = new UserRegisterRequest(
            "test",
            email,
            "password",
            ""
        );

        User user = User.createOf(request, "myencodedpassword");
        return userRepository.saveAndFlush(user);
    }
}