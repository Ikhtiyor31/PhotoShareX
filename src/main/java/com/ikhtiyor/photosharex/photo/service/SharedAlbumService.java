package com.ikhtiyor.photosharex.photo.service;

import com.ikhtiyor.photosharex.user.model.User;

public interface SharedAlbumService {

    void createSharedAlbum(Long albumId, User user);
}
