package com.ikhtiyor.photosharex.photo.service;

import com.ikhtiyor.photosharex.photo.dto.PhotoRequest;
import com.ikhtiyor.photosharex.photo.dto.PhotoUpdateRequest;
import com.ikhtiyor.photosharex.photo.dto.UploadPhotoDTO;
import com.ikhtiyor.photosharex.photo.enums.VisibilityType;
import com.ikhtiyor.photosharex.user.model.User;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface PhotoService {

    void createPhoto(PhotoRequest request, User user);
    UploadPhotoDTO uploadImage(MultipartFile image);
    Resource downloadImage(String imageName);

    void updatePhotoDetail(PhotoUpdateRequest request, Long photoId, User user);

    void changePhotoVisibility(Long photoId, VisibilityType visibilityType, User user);
}
