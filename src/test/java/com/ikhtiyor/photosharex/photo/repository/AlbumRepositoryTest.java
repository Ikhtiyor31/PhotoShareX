package com.ikhtiyor.photosharex.photo.repository;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.ikhtiyor.photosharex.photo.dto.AlbumRequest;
import com.ikhtiyor.photosharex.photo.model.Album;
import com.ikhtiyor.photosharex.user.dto.UserRegisterRequest;
import com.ikhtiyor.photosharex.user.model.User;
import com.ikhtiyor.photosharex.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@DataJpaTest
class AlbumRepositoryTest {

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        albumRepository.deleteAll();
        userRepository.deleteAll();
        entityManager.clear();
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

    @Test
    void givenAlbum_whenFindByUser_thenReturnsOnlyMyAlbums() {
        // Given
        AlbumRequest albumRequest = new AlbumRequest(
            "My new album",
            "this is my description about album"
        );
        String email1 = "test1@gmail.com";
        String email2 = "test2@gmail.com";
        User user1 = getUser(email1);
        User user2 = getUser(email2);

        Album album1 = Album.of(albumRequest, user1);
        Album album2 = Album.of(albumRequest, user2);
        albumRepository.save(album1);
        albumRepository.save(album2);

        PageRequest pageable = PageRequest.of(0, 10, Sort.Direction.DESC, "createdAt");
        final var fetchAlbum = albumRepository.findByUser(user1, pageable);
        assertFalse(fetchAlbum.isEmpty());
        assertThat(fetchAlbum.getTotalElements()).isEqualTo(1);
    }

    @Test
    void givenAlbum_whenFindByUser_thenReturnsNull() {
        // Given
        AlbumRequest albumRequest = new AlbumRequest(
            "My new album",
            "this is my description about album"
        );
        String email1 = "test1@gmail.com";
        String email2 = "test2@gmail.com";
        User user1 = getUser(email1);
        User user2 = getUser(email2);

        Album album1 = Album.of(albumRequest, user1);
        Album album2 = Album.of(albumRequest, user1);
        albumRepository.save(album1);
        albumRepository.save(album2);

        PageRequest pageable = PageRequest.of(0, 10, Sort.Direction.DESC, "createdAt");
        final var fetchAlbum = albumRepository.findByUser(user2, pageable);
        assertTrue(fetchAlbum.isEmpty());
        assertThat(fetchAlbum.getTotalElements()).isEqualTo(0);
    }

    @Test
    void givenAlbum_whenUpdateAlbum_thenReturnsUpdateAlbum() {
        // Given
        AlbumRequest albumRequest = new AlbumRequest(
            "My album",
            "this is my description about album"
        );
        String email = "test1@gmail.com";
        User user = getUser(email);

        Album album = Album.of(albumRequest, user);
        albumRepository.save(album);
        AlbumRequest albumRequest2 = new AlbumRequest(
            "My updated album",
            "this is my updated description about album"
        );
        final var fetchAlbum = albumRepository.findByUserAndId(user, album.getId()).get();
        fetchAlbum.setTitle(albumRequest2.title());
        fetchAlbum.setDescription(albumRequest2.description());
        final var fetchUpdateAlbum = albumRepository.findByUserAndId(user, album.getId()).get();

        assertThat(fetchUpdateAlbum.getTitle()).isEqualTo(albumRequest2.title());
        assertThat(fetchUpdateAlbum.getDescription()).isEqualTo(albumRequest2.description());
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