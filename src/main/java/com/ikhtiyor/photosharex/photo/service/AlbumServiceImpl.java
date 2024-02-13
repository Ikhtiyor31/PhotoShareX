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
import com.ikhtiyor.photosharex.utils.StringUtil;
import java.util.ArrayList;
import java.util.List;
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
    public String addPhotosToAlbum(Long albumId, PhotoIdsRequest request, User user) {
        Album album = getAlbum(albumId);
        List<Photo> photos = getPhotos(request, user);

        List<PhotoAlbum> photoAlbums = new ArrayList<>();

        photos.forEach(photo -> {
            PhotoAlbumId photoAlbumId = new PhotoAlbumId(album.getId(), photo.getId());
            PhotoAlbum photoAlbum = new PhotoAlbum(photoAlbumId, photo, album);
            photoAlbums.add(photoAlbum);
        });
        List<PhotoAlbum> savedPhotoAlbums = photoAlbumRepository.saveAll(photoAlbums);

        if (album.getCoverImageUrl().isEmpty()) {
            updateAlbumCoverImage(album, savedPhotoAlbums.get(0).getPhoto().getImageUrl());
        }

        return StringUtil.formatItemAddMessage(savedPhotoAlbums.size());
    }

    @Override
    public void updateAlbumCoverImage(Long albumId, String coverImageUrl, User user) {
        Album album = albumRepository.findByUserAndId(user, albumId).orElseThrow(
            () -> new ResourceNotFoundException("Album not found with ID: " + albumId)
        );

        updateAlbumCoverImage(album, coverImageUrl);
    }

    private Album getAlbum(Long albumId) {
        return albumRepository.findById(albumId).orElseThrow(
            () -> new ResourceNotFoundException("Album not found with ID: " + albumId));
    }

    private List<Photo> getPhotos(PhotoIdsRequest request, User user) {
        List<Photo> photos = photoRepository.findByUserAndIdIn(user, request.photoIds());

        if (photos.isEmpty()) {
            throw new ResourceNotFoundException("No photos found for the provided photoIds");
        }

        return photos;
    }

    private void updateAlbumCoverImage(Album album, String coverImageUrl) {
        album.updateCoverImage(coverImageUrl);
    }
}
