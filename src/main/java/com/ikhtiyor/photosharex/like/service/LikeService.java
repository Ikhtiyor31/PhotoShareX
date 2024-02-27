package com.ikhtiyor.photosharex.like.service;

import com.ikhtiyor.photosharex.user.model.User;

public interface LikeService {

    void createLike(Long albumId, Long photoId, User user);

    void removeLike(Long likeId);
}
