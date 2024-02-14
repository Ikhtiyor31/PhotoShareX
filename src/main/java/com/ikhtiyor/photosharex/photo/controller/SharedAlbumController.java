package com.ikhtiyor.photosharex.photo.controller;

import com.ikhtiyor.photosharex.annotation.Authenticated;
import com.ikhtiyor.photosharex.photo.service.SharedAlbumService;
import com.ikhtiyor.photosharex.security.UserAdapter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/shared-albums")
public class SharedAlbumController {

    private final SharedAlbumService sharedAlbumService;

    public SharedAlbumController(SharedAlbumService sharedAlbumService) {
        this.sharedAlbumService = sharedAlbumService;
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/{albumId}")
    public ResponseEntity<Void> createSharedAlbum(
        @PathVariable Long albumId,
        @Authenticated UserAdapter userAdapter
    ) {

        sharedAlbumService.createSharedAlbum(albumId, userAdapter.getUser());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

}
