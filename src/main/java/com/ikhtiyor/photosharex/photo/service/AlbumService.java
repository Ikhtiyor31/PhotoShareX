package com.ikhtiyor.photosharex.photo.service;

import com.ikhtiyor.photosharex.photo.dto.AddPhotosToAlbumRequest;
import com.ikhtiyor.photosharex.photo.dto.AlbumRequest;
import com.ikhtiyor.photosharex.user.model.User;

public interface AlbumService {

    void createAlbum(AlbumRequest albumRequest, User user);

    void addPhotosToAlbum(AddPhotosToAlbumRequest request, User user);
}
