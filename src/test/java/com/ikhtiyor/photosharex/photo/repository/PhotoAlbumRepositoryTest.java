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
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;

@DataJpaTest
class PhotoAlbumRepositoryTest {

    @Autowired
    private PhotoAlbumRepository photoAlbumRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PhotoRepository photoRepository;

    @Autowired
    private EntityManager entityManager;


    @BeforeEach
    void setUp() {
        photoAlbumRepository.deleteAll();
        userRepository.deleteAll();
        photoRepository.deleteAll();
        entityManager.clear();
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

        AlbumRequest albumRequest = new AlbumRequest("My new Album",
            "this is my description about Album");

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

    @Test
    void givenPhotoAlbum_whenFindPhotoAlbumByPhotoAlbumId_thenReturnsPhotoAlbum() {
        PhotoRequest photoRequest = new PhotoRequest(
            "http://localhost:8080/image_2024_02_11_23423.jpg",
            "My old image",
            "This is a beautiful old image",
            VisibilityType.PUBLIC,
            "Seoul"
        );
        UserRegisterRequest request = new UserRegisterRequest(
            "test",
            "test@gmail.com",
            "password",
            ""
        );

        User user = User.createOf(request, request.password());

        Photo photo = Photo.createOf(photoRequest, user);

        AlbumRequest albumRequest = new AlbumRequest(
            "My new album",
            "this is my description about album"
        );

        Album album = Album.of(albumRequest, user);
        user.addAlbum(album);
        user.addPhoto(photo);
        userRepository.saveAndFlush(user);
        PhotoAlbumId photoAlbumId = new PhotoAlbumId(photo.getId(), album.getId());
        PhotoAlbum photoAlbum = new PhotoAlbum(photoAlbumId, photo, album);
        photoAlbumRepository.saveAndFlush(photoAlbum);

        var fetchPhotoAlbum = photoAlbumRepository.findPhotoAlbumByPhotoAlbumId(photoAlbumId);
        assertThat(fetchPhotoAlbum).isNotEmpty();
        assertTrue(fetchPhotoAlbum.isPresent());
        assertThat(fetchPhotoAlbum.get().getAlbum().getId()).isEqualTo(album.getId());
        assertThat(fetchPhotoAlbum.get().getPhoto().getId()).isEqualTo(photo.getId());
        assertThat(fetchPhotoAlbum.get().getPhotoAlbumId()).isEqualTo(photoAlbum.getPhotoAlbumId());
    }

    @Test
    void givenPhotoAlbum_whenFindPhotoAlbumByPhotoAlbumId_withDifferentUserPhotoAlbum_thenReturnsNull() {
        PhotoRequest photoRequest = new PhotoRequest(
            "http://localhost:8080/image_2024_02_11_23423.jpg",
            "My old image",
            "This is a beautiful old image",
            VisibilityType.PUBLIC,
            "Seoul"
        );
        UserRegisterRequest userRegisterRequest = new UserRegisterRequest(
            "test",
            "test@gmail.com",
            "password",
            ""
        );

        User user = User.createOf(userRegisterRequest, userRegisterRequest.password());

        Photo photo = Photo.createOf(photoRequest, user);

        AlbumRequest albumRequest = new AlbumRequest(
            "My new album",
            "this is my description about album"
        );

        Album album = Album.of(albumRequest, user);
        user.addAlbum(album);
        user.addPhoto(photo);
        userRepository.saveAndFlush(user);
        PhotoAlbumId photoAlbumId = new PhotoAlbumId(photo.getId(), album.getId());
        PhotoAlbum photoAlbum = new PhotoAlbum(photoAlbumId, photo, album);
        photoAlbumRepository.saveAndFlush(photoAlbum);

        UserRegisterRequest userRegisterRequest2 = new UserRegisterRequest(
            "differentUser",
            "differentuser@gmail.com",
            "password",
            ""
        );
        User differentUser = User.createOf(userRegisterRequest2, userRegisterRequest2.password());
        Photo photo1 = Photo.createOf(photoRequest, differentUser);
        Album album1 = Album.of(albumRequest, differentUser);
        differentUser.addPhoto(photo1);
        differentUser.addAlbum(album1);
        userRepository.save(differentUser);
        PhotoAlbumId photoAlbumId1 = new PhotoAlbumId(photo1.getId(), album1.getId());
        PhotoAlbum photoAlbum1 = new PhotoAlbum(photoAlbumId1, photo1, album1);
        photoAlbumRepository.saveAndFlush(photoAlbum1);

        Optional<PhotoAlbum> photoAlbumByPhotoAlbumId1 = photoAlbumRepository.findPhotoAlbumByPhotoAlbumId(
            new PhotoAlbumId(photo1.getId(), album.getId()));

        Optional<PhotoAlbum> photoAlbumByPhotoAlbumId2 = photoAlbumRepository.findPhotoAlbumByPhotoAlbumId(
            new PhotoAlbumId(photo.getId(), album1.getId()));

        assertFalse(photoAlbumByPhotoAlbumId1.isPresent());
        assertFalse(photoAlbumByPhotoAlbumId2.isPresent());
    }

    @Test
    void givenPhotoAlbum_whenDeleteAllById_thenDeleteAllPhotosByGivenId() {
        // Given
        PhotoRequest photoRequest = new PhotoRequest(
            "http://localhost:8080/image_2024_02_11_23423.jpg",
            "My old image",
            "This is a beautiful old image",
            VisibilityType.PUBLIC,
            "Seoul"
        );
        UserRegisterRequest userRegisterRequest = new UserRegisterRequest(
            "test",
            "test@gmail.com",
            "password",
            ""
        );

        User user = User.createOf(userRegisterRequest, userRegisterRequest.password());
        Photo photo = Photo.createOf(photoRequest, user);
        AlbumRequest albumRequest = new AlbumRequest(
            "My new album",
            "this is my description about album"
        );

        Album album = Album.of(albumRequest, user);
        user.addAlbum(album);
        user.addPhoto(photo);
        user.addPhoto(photo);
        user.addPhoto(photo);
        user.addPhoto(photo);
        user.addPhoto(photo);
        userRepository.saveAndFlush(user);
        PhotoAlbumId photoAlbumId = new PhotoAlbumId(photo.getId(), album.getId());
        PhotoAlbum photoAlbum = new PhotoAlbum(photoAlbumId, photo, album);
        photoAlbumRepository.saveAndFlush(photoAlbum);

        List<PhotoAlbum> photoAlbumsByAlbumIds = photoAlbumRepository
            .findPhotoAlbumsByAlbum_Id(album.getId(), Pageable.unpaged()).toList();

        List<PhotoAlbumId> photoAlbumIdList = new ArrayList<>();
        photoAlbumsByAlbumIds.forEach(
            photoAlbumIds -> photoAlbumIdList.add(photoAlbumIds.getPhotoAlbumId()));

        // When
        photoAlbumRepository.deleteAllById(photoAlbumIdList);
        final var fetchPhotoAlbumListAfterDeleted = photoAlbumRepository
            .findPhotoAlbumsByAlbum_Id(album.getId(), Pageable.unpaged());

        // Then
        assertTrue(fetchPhotoAlbumListAfterDeleted.isEmpty());
        assertThat(fetchPhotoAlbumListAfterDeleted.getTotalElements()).isEqualTo(0);
        assertTrue(fetchPhotoAlbumListAfterDeleted.getContent().isEmpty());
    }
}