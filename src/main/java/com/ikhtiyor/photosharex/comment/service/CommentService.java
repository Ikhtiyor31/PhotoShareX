package com.ikhtiyor.photosharex.comment.service;

import com.ikhtiyor.photosharex.comment.dto.CommentDTO;
import com.ikhtiyor.photosharex.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentService {

    void createComment(Long albumId, Long photoId, User user, String message);

    Page<CommentDTO> getComments(Long photoId, Pageable pageable, User user);

    void deleteComment(Long commentId);
}
