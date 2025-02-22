package com.ikhtiyor.photosharex.comment.service;

import com.ikhtiyor.photosharex.comment.dto.CommentDTO;
import com.ikhtiyor.photosharex.comment.model.Comment;
import com.ikhtiyor.photosharex.comment.repository.CommentRepository;
import com.ikhtiyor.photosharex.exception.ResourceNotFoundException;
import com.ikhtiyor.photosharex.exception.UnauthorizedActionException;
import com.ikhtiyor.photosharex.photo.model.Album;
import com.ikhtiyor.photosharex.photo.model.Photo;
import com.ikhtiyor.photosharex.photo.repository.AlbumRepository;
import com.ikhtiyor.photosharex.photo.repository.PhotoRepository;
import com.ikhtiyor.photosharex.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CommentServiceImpl implements CommentService {

    private final AlbumRepository albumRepository;
    private final PhotoRepository photoRepository;
    private final CommentRepository commentRepository;

    public CommentServiceImpl(AlbumRepository albumRepository,
        PhotoRepository photoRepository,
        CommentRepository commentRepository
    ) {
        this.albumRepository = albumRepository;
        this.photoRepository = photoRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    public void createComment(Long albumId, Long photoId, User user, String message) {
        Photo photo = photoRepository.findById(photoId)
            .orElseThrow(() -> new ResourceNotFoundException("Photo not found wih Id: " + photoId));

        Album album = albumRepository.findByUserAndId(user, albumId)
            .orElseThrow(() -> new ResourceNotFoundException("Album not found with ID:" + albumId));

        if (isPhotoInSharedAlbum(album, user)) {
            throw new UnauthorizedActionException(
                "You can only comment on photos in shared albums");
        }

        Comment comment = Comment.createOf(photo, user, message);
        commentRepository.save(comment);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<CommentDTO> getComments(Long photoId, Pageable pageable, User user) {
        return commentRepository.findByPhoto_IdAndUser(photoId, user, pageable)
            .map(CommentDTO::fromEntity);
    }

    @Override
    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(
            () -> new ResourceNotFoundException("Comment not found with ID: " + commentId));

        comment.setDeleted();
    }

    public boolean isPhotoInSharedAlbum(Album album, User user) {
        return album.isShared() || album.getSharedUsers().contains(user);
    }
}
