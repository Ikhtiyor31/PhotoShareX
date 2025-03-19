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
    void shouldSaveAlbumSuccessfully() {
        Album album = createAlbum("tourist@gmail.com", "My new album",
            "This is my description about the album.");
        assertThat(album).isNotNull();
        assertThat(album.getId()).isGreaterThan(0);
    }

    @Test
    void shouldUpdateAlbumCoverImageSuccessfully() {
        Album album = createAlbum("test@gmail.com", "My new album",
            "This is my description about the album.");
        String albumCoverImageUrl = "http://localhost:8080/my-album-cover-image.jpg";

        album.updateCoverImage(albumCoverImageUrl);
        assertThat(album.getCoverImageUrl()).isEqualTo(albumCoverImageUrl);
    }

    @Test
    void shouldFindAlbumByUserAndId() {
        User user = getUser("someone@gmail.com");
        Album savedAlbum = createAlbum(user, "My new album",
            "This is my description about the album.");

        Optional<Album> fetchAlbum = albumRepository.findByUserAndId(user, savedAlbum.getId());
        assertThat(fetchAlbum).isPresent().contains(savedAlbum);
    }

    @Test
    void shouldNotFindAlbumIfBelongsToDifferentUser() {
        User owner = getUser("test1@gmail.com");
        User anotherUser = getUser("test2@gmail.com");

        Album savedAlbum = createAlbum(owner, "My new album",
            "This is my description about the album.");
        Optional<Album> fetchAlbum = albumRepository.findByUserAndId(anotherUser,
            savedAlbum.getId());

        assertThat(fetchAlbum).isEmpty();
    }

    @Test
    void shouldReturnAlbumsOwnedByUser() {
        User user1 = getUser("test1@gmail.com");
        User user2 = getUser("test2@gmail.com");

        createAlbum(user1, "Album 1", "Description");
        createAlbum(user2, "Album 2", "Description");

        PageRequest pageable = PageRequest.of(0, 10, Sort.Direction.DESC, "createdAt");
        var fetchAlbums = albumRepository.findByUser(user1, pageable);

        assertFalse(fetchAlbums.isEmpty());
        assertThat(fetchAlbums.getTotalElements()).isEqualTo(1);
    }

    @Test
    void shouldReturnEmptyIfUserHasNoAlbums() {
        User user1 = getUser("test1@gmail.com");
        User user2 = getUser("test2@gmail.com");

        createAlbum(user1, "Album 1", "Description");
        createAlbum(user1, "Album 2", "Description");

        PageRequest pageable = PageRequest.of(0, 10, Sort.Direction.DESC, "createdAt");
        var fetchAlbums = albumRepository.findByUser(user2, pageable);

        assertTrue(fetchAlbums.isEmpty());
        assertThat(fetchAlbums.getTotalElements()).isEqualTo(0);
    }

    @Test
    void shouldUpdateAlbumSuccessfully() {
        User user = getUser("test1@gmail.com");
        Album album = createAlbum(user, "My album", "This is my description about the album.");

        AlbumRequest updatedRequest = new AlbumRequest("My updated album",
            "This is my updated description.");

        Album fetchAlbum = albumRepository.findByUserAndId(user, album.getId()).orElseThrow();
        fetchAlbum.setTitle(updatedRequest.title());
        fetchAlbum.setDescription(updatedRequest.description());

        Album updatedAlbum = albumRepository.findByUserAndId(user, album.getId()).orElseThrow();
        assertThat(updatedAlbum.getTitle()).isEqualTo(updatedRequest.title());
        assertThat(updatedAlbum.getDescription()).isEqualTo(updatedRequest.description());
    }

    @Test
    void shouldInviteUserToAlbumSuccessfully() {
        User owner = getUser("owner@gmail.com");
        User invitedUser = getUser("invitee@gmail.com");
        Album album = createAlbum(owner, "Shared Album", "An album to be shared.");

        // Invite the user
        album.getSharedUsers().add(invitedUser);
        albumRepository.saveAndFlush(album);
        entityManager.flush();
        entityManager.clear();

        // Verify user was added
        Album updatedAlbum = albumRepository.findById(album.getId()).orElseThrow();
        assertThat(updatedAlbum.getSharedUsers().size()).isEqualTo(1);
        assertThat(updatedAlbum.getSharedUsers().contains(invitedUser)).isEqualTo(true);
    }

    private Album createAlbum(String email, String title, String description) {
        User user = getUser(email);
        return createAlbum(user, title, description);
    }

    private Album createAlbum(User user, String title, String description) {
        AlbumRequest albumRequest = new AlbumRequest(title, description);
        Album album = Album.of(albumRequest, user);
        return albumRepository.save(album);
    }

    private User getUser(String email) {
        UserRegisterRequest request = new UserRegisterRequest("test", email, "password", "");
        User user = User.createOf(request, "myencodedpassword");
        return userRepository.saveAndFlush(user);
    }

}