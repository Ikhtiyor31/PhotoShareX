package com.ikhtiyor.photosharex.photo.service;

import com.ikhtiyor.photosharex.exception.ResourceNotFoundException;
import com.ikhtiyor.photosharex.photo.dto.PhotoIdsRequest;
import com.ikhtiyor.photosharex.photo.dto.AlbumRequest;
import com.ikhtiyor.photosharex.photo.model.Album;
import com.ikhtiyor.photosharex.photo.model.Photo;
import com.ikhtiyor.photosharex.photo.model.PhotoAlbum;
import com.ikhtiyor.photosharex.photo.model.PhotoAlbumId;
import com.ikhtiyor.photosharex.photo.repository.AlbumRepository;
import com.ikhtiyor.photosharex.photo.repository.PhotoAlbumRepository;
import com.ikhtiyor.photosharex.photo.repository.PhotoRepository;
import com.ikhtiyor.photosharex.user.model.User;
import java.util.ArrayList;
import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AlbumServiceImpl implements AlbumService {

    private final AlbumRepository albumRepository;
    private final PhotoRepository photoRepository;

    private final PhotoAlbumRepository photoAlbumRepository;

    public AlbumServiceImpl(AlbumRepository albumRepository, PhotoRepository photoRepository,
        PhotoAlbumRepository photoAlbumRepository) {
        this.albumRepository = albumRepository;
        this.photoRepository = photoRepository;
        this.photoAlbumRepository = photoAlbumRepository;
    }

    @Override
    public void createAlbum(AlbumRequest albumRequest, User user) {
        Album album = Album.of(albumRequest, user);
        albumRepository.save(album);
    }

    @Override
    public void addPhotosToAlbum(Long albumId, PhotoIdsRequest request, User user) {
        Album album = albumRepository.findById(albumId).orElseThrow(
            () -> new ResourceNotFoundException("Album not found with ID: " + albumId));

        List<Photo> photos = photoRepository.findByUserAndIdIn(user, request.photoIds());

        if (photos.isEmpty()) {
            throw new ResourceNotFoundException("No photos found for the provided photoIds");
        }

        List<PhotoAlbum> photoAlbums = new ArrayList<>();

        photos.forEach(photo -> {
            PhotoAlbumId photoAlbumId = new PhotoAlbumId(album.getId(), photo.getId());
            PhotoAlbum photoAlbum = new PhotoAlbum(photoAlbumId, photo, album);
            photoAlbums.add(photoAlbum);
        });

        photoAlbumRepository.saveAll(photoAlbums);
    }
}
