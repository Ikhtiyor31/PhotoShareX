package com.ikhtiyor.photosharex.photo.service;

import com.ikhtiyor.photosharex.photo.dto.AlbumRequest;
import com.ikhtiyor.photosharex.photo.model.Album;
import com.ikhtiyor.photosharex.photo.repository.AlbumRepository;
import com.ikhtiyor.photosharex.user.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AlbumServiceImpl implements AlbumService {

    private final AlbumRepository albumRepository;

    public AlbumServiceImpl(AlbumRepository albumRepository) {
        this.albumRepository = albumRepository;
    }

    @Override
    public void createAlbum(AlbumRequest albumRequest, User user) {
        Album album = Album.of(albumRequest, user);
        albumRepository.save(album);
    }
}
