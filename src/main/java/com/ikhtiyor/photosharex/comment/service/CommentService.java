package com.ikhtiyor.photosharex.comment.service;

import com.ikhtiyor.photosharex.user.model.User;

public interface CommentService {

    void createComment(Long albumId, Long photoId, User user, String message);
}
