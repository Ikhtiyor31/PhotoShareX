package com.ikhtiyor.photosharex.photo.repository;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.ikhtiyor.photosharex.photo.dto.AlbumRequest;
import com.ikhtiyor.photosharex.photo.model.Album;
import com.ikhtiyor.photosharex.user.dto.UserRegisterRequest;
import com.ikhtiyor.photosharex.user.model.User;
import com.ikhtiyor.photosharex.user.repository.UserRepository;
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
        Album album = Album.of(albumRequest, getUser());
        final var savedAlbum = albumRepository.save(album);
        assertThat(savedAlbum).isNotNull();
        assertThat(savedAlbum.getId()).isGreaterThan(0);
    }


    public User getUser() {
        UserRegisterRequest request = new UserRegisterRequest(
            "test",
            "test@gmail.com",
            "password",
            ""
        );

        User user = User.createOf(request, "myencodedpassword");
        return userRepository.saveAndFlush(user);
    }
}