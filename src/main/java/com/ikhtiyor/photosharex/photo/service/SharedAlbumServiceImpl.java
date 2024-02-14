package com.ikhtiyor.photosharex.photo.service;

import com.ikhtiyor.photosharex.exception.ResourceNotFoundException;
import com.ikhtiyor.photosharex.photo.model.Album;
import com.ikhtiyor.photosharex.photo.model.SharedAlbum;
import com.ikhtiyor.photosharex.photo.repository.AlbumRepository;
import com.ikhtiyor.photosharex.photo.repository.SharedAlbumRepository;
import com.ikhtiyor.photosharex.user.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SharedAlbumServiceImpl implements SharedAlbumService {

    private final SharedAlbumRepository sharedAlbumRepository;
    private final AlbumRepository albumRepository;

    public SharedAlbumServiceImpl(SharedAlbumRepository sharedAlbumRepository,
        AlbumRepository albumRepository) {
        this.sharedAlbumRepository = sharedAlbumRepository;
        this.albumRepository = albumRepository;
    }

    @Override
    public void createSharedAlbum(Long albumId, User user) {
        Album album = albumRepository.findById(albumId).orElseThrow(
            () -> new ResourceNotFoundException("Album not found with ID: " + albumId));

        markAlbumAsShared(album);

        SharedAlbum sharedAlbum = SharedAlbum.createOf(album, user);
        sharedAlbumRepository.save(sharedAlbum);
    }

    private void markAlbumAsShared(Album album) {
        if (!album.isShared()) {
            album.setShared();
        }
    }
}
