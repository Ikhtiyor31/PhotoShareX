package com.ikhtiyor.photosharex.photo.service;

import com.ikhtiyor.photosharex.exception.ResourceNotFoundException;
import com.ikhtiyor.photosharex.photo.dto.AlbumDTO;
import com.ikhtiyor.photosharex.photo.dto.PhotoDTO;
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
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
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
            updateAlbumCoverImage(album, savedPhotoAlbums.get(0).getPhoto());
        }

        return StringUtil.formatItemAddMessage(savedPhotoAlbums.size());
    }

    @Override
    public void updateAlbumCoverImage(Long albumId, Long photoId, User user) {
        Album album = getAlbumByUserAndId(albumId, user);
        Photo photo = photoRepository.findByUserAndId(user, photoId).orElseThrow(
            () -> new ResourceNotFoundException("Photo not found with ID: " + photoId));

        PhotoAlbumId photoAlbumId = new PhotoAlbumId(photo.getId(), album.getId());
        PhotoAlbum photoAlbum = photoAlbumRepository.findPhotoAlbumByPhotoAlbumId(photoAlbumId)
            .orElseThrow(
                () -> new ResourceNotFoundException("PhotoAlbum not found with provided ID"));

        updateAlbumCoverImage(photoAlbum.getAlbum(), photoAlbum.getPhoto());
    }

    @Override
    public Page<AlbumDTO> getMyAlbums(Pageable pageable, User user) {
        PageRequest createdAtPageable = PageRequest.of(
            pageable.getPageNumber(),
            pageable.getPageSize(),
            Direction.DESC, "createdAt");

        return albumRepository.findByUser(user, createdAtPageable)
            .map(AlbumDTO::from);
    }

    @Override
    public Page<PhotoDTO> getAlbumPhotos(Pageable pageable, Long albumId, User user) {
        Album album = getAlbumByUserAndId(albumId, user);

        PageRequest createdAtPageable = PageRequest.of(
            pageable.getPageNumber(),
            pageable.getPageSize(),
            Direction.DESC, "createdAt");

        Page<PhotoAlbum> photoAlbumPage = photoAlbumRepository.findPhotoAlbumsByAlbum_Id(
            album.getId(),
            createdAtPageable
        );
        List<Photo> photos = photoAlbumPage.getContent().stream()
            .map(PhotoAlbum::getPhoto)
            .collect(Collectors.toList());

        return new PageImpl<>(photos, pageable, photoAlbumPage.getTotalElements()).map(
            PhotoDTO::from);
    }

    @Override
    public String removePhotosFromAlbum(Long albumId, PhotoIdsRequest request, User user) {
        Album album = getAlbumByUserAndId(albumId, user);
        List<PhotoAlbumId> photoAlbumIds = new ArrayList<>();
        request.photoIds()
            .forEach(photoId -> photoAlbumIds.add(new PhotoAlbumId(photoId, album.getId())));
        photoAlbumRepository.deleteAllById(photoAlbumIds);

        return StringUtil.formatItemRemoveMessage(photoAlbumIds.size());
    }

    private Album getAlbumByUserAndId(Long albumId, User user) {
        return albumRepository.findByUserAndId(user, albumId).orElseThrow(
            () -> new ResourceNotFoundException("Album not found with ID: " + albumId));
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

    private void updateAlbumCoverImage(Album album, Photo photo) {
        album.updateCoverImage(photo.getImageUrl());
    }
}
