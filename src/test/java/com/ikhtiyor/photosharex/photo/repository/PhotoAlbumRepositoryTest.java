package com.ikhtiyor.photosharex.photo.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.ikhtiyor.photosharex.photo.dto.AlbumRequest;
import com.ikhtiyor.photosharex.photo.dto.PhotoRequest;
import com.ikhtiyor.photosharex.photo.enums.VisibilityType;
import com.ikhtiyor.photosharex.photo.model.Album;
import com.ikhtiyor.photosharex.photo.model.Photo;
import com.ikhtiyor.photosharex.photo.model.PhotoAlbum;
import com.ikhtiyor.photosharex.photo.model.PhotoAlbumId;
import com.ikhtiyor.photosharex.user.dto.UserRegisterRequest;
import com.ikhtiyor.photosharex.user.model.User;
import com.ikhtiyor.photosharex.user.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class PhotoAlbumRepositoryTest {

    @Autowired
    private PhotoAlbumRepository photoAlbumRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PhotoRepository photoRepository;


    @BeforeEach
    void setUp() {
        photoAlbumRepository.deleteAll();
        userRepository.deleteAll();
        photoRepository.deleteAll();
    }

    @Test
    void whenAddPhotosToAlbum_thenReturnSuccess() {
        // Given
        UserRegisterRequest request = new UserRegisterRequest(
            "test",
            "test@gmail.com",
            "password",
            ""
        );

        User user = User.createOf(request, "myencodedpassword");

        AlbumRequest albumRequest = new AlbumRequest("My new Album", "this is my description about Album");

        user.addAlbum(Album.of(albumRequest, user));
        PhotoRequest photoRequest = new PhotoRequest(
            "http://localhost:8080/image_2024_02_11_23423.jpg",
            "My old image",
            "This is a beautiful old image",
            VisibilityType.PUBLIC,
            "Seoul"
        );
        Photo photo1 = Photo.createOf(photoRequest, user);
        Photo photo2 = Photo.createOf(photoRequest, user);
        Photo photo3 = Photo.createOf(photoRequest, user);
        user.addPhoto(photo1);
        user.addPhoto(photo2);
        user.addPhoto(photo3);

        final var savedUser = userRepository.save(user);
        List<Photo> photos = photoRepository.findAll();

        List<PhotoAlbum> photoAlbums = new ArrayList<>();
        photos.forEach(photo -> {
            Album album = savedUser.getAlbums().get(0);
            PhotoAlbumId photoAlbumId = new PhotoAlbumId(album.getId(), photo.getId());
            PhotoAlbum photoAlbum = new PhotoAlbum(photoAlbumId, photo, album);
            photoAlbums.add(photoAlbum);
        });

        List<PhotoAlbum> savedPhotoAlbums = photoAlbumRepository.saveAll(photoAlbums);

        assertFalse(photos.isEmpty());
        assertThat(photos.size()).isGreaterThan(2);
        assertThat(savedPhotoAlbums.size()).isGreaterThan(2);
    }
}