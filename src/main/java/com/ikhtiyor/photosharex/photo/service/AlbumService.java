package com.ikhtiyor.photosharex.photo.service;

import com.ikhtiyor.photosharex.photo.dto.AlbumDTO;
import com.ikhtiyor.photosharex.photo.dto.AlbumRequest;
import com.ikhtiyor.photosharex.photo.dto.PhotoDTO;
import com.ikhtiyor.photosharex.photo.dto.PhotoIdsRequest;
import com.ikhtiyor.photosharex.user.model.User;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AlbumService {

    void createAlbum(AlbumRequest albumRequest, User user);

    String addPhotosToAlbum(Long albumId, PhotoIdsRequest request, User user);

    void updateAlbumCoverImage(Long albumId, Long photoId, User user);

    Page<AlbumDTO> getMyAlbums(Pageable pageable, User user);

    Page<PhotoDTO> getPhotosByAlbum(Pageable pageable, Long albumId, User user);

    String removePhotosFromAlbum(Long albumId, PhotoIdsRequest request, User user);

    void updateAlbum(Long albumId, AlbumRequest albumRequest, User user);

    AlbumDTO inviteUser(Long albumId, Long userId);

    AlbumDTO getAlbum(Long albumId);

    void deleteAlbum(Long albumId, User user);

    List<AlbumDTO> listSharedAlbums(User user);

    void revokeAccess(Long albumId, Long userId, User user);
}
