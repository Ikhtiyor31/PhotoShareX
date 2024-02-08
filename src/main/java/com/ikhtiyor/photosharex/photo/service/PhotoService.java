package com.ikhtiyor.photosharex.photo.service;

import com.ikhtiyor.photosharex.photo.dto.PhotoRequest;
import com.ikhtiyor.photosharex.user.model.User;

public interface PhotoService {

    void createPhoto(PhotoRequest request, User user);
}
