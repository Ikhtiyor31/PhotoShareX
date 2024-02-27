package com.ikhtiyor.photosharex.like.service;


import com.ikhtiyor.photosharex.exception.ResourceNotFoundException;
import com.ikhtiyor.photosharex.like.model.Like;
import com.ikhtiyor.photosharex.like.repository.LikeRepository;
import com.ikhtiyor.photosharex.photo.model.Album;
import com.ikhtiyor.photosharex.photo.model.Photo;
import com.ikhtiyor.photosharex.photo.repository.AlbumRepository;
import com.ikhtiyor.photosharex.photo.repository.PhotoRepository;
import com.ikhtiyor.photosharex.user.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LikeServiceImpl implements LikeService {

    private final PhotoRepository photoRepository;
    private final AlbumRepository albumRepository;
    private final LikeRepository likeRepository;

    public LikeServiceImpl(PhotoRepository photoRepository, AlbumRepository albumRepository,
        LikeRepository likeRepository) {
        this.photoRepository = photoRepository;
        this.albumRepository = albumRepository;
        this.likeRepository = likeRepository;
    }

    @Override
    public void createLike(Long albumId, Long photoId, User user) {
        Photo photo = photoRepository.findById(photoId)
            .orElseThrow(() -> new ResourceNotFoundException("Photo not found wih Id: " + photoId));

        Album album = albumRepository.findByUserAndId(user, albumId)
            .orElseThrow(() -> new ResourceNotFoundException("Album not found with ID:" + albumId));

        if (!album.isShared()) {
            throw new IllegalArgumentException("SharedAlbum not found");
        }

        Like like = Like.createOf(photo, user);
        likeRepository.save(like);
    }

    @Override
    public void removeLike(Long likeId) {
        Like like = likeRepository.findById(likeId).orElseThrow(
            () -> new ResourceNotFoundException("Like not found with ID: " + likeId));

        like.setDeleted();
    }
}
