package com.ikhtiyor.photosharex.photo.controller;


import com.ikhtiyor.photosharex.annotation.Authenticated;
import com.ikhtiyor.photosharex.photo.dto.PhotoIdsRequest;
import com.ikhtiyor.photosharex.photo.dto.AlbumRequest;
import com.ikhtiyor.photosharex.photo.service.AlbumService;
import com.ikhtiyor.photosharex.security.UserAdapter;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/albums")
@Validated
public class AlbumController {

    private final AlbumService albumService;

    public AlbumController(AlbumService albumService) {
        this.albumService = albumService;
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public ResponseEntity<String> createAlbum(
        @RequestBody @Valid AlbumRequest albumRequest,
        @Authenticated UserAdapter userAdapter
    ) {
        albumService.createAlbum(albumRequest, userAdapter.getUser());
        return ResponseEntity.status(HttpStatus.CREATED)
            .body("Album created!");
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/{albumId}")
    public ResponseEntity<String> addPhotosToAlbum(
        @PathVariable @Min(value = 1, message = "albumId field cannot be null or empty") Long albumId,
        @RequestBody PhotoIdsRequest request,
        @Authenticated UserAdapter userAdapter
    ) {
        final var itemAddedMessage = albumService.addPhotosToAlbum(albumId, request, userAdapter.getUser());
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(itemAddedMessage);
    }

    @PreAuthorize("hasRole('USER')")
    @PatchMapping("/{albumId}/photos/{photoId}")
    public ResponseEntity<String> updateAlbumCoverImage(
        @PathVariable @Min(value = 1, message = "albumId field cannot be null or empty") Long albumId,
        @PathVariable @Min(value = 1, message = "photoId field cannot be null or empty") Long photoId,
        @Authenticated UserAdapter userAdapter
    ) {
        albumService.updateAlbumCoverImage(albumId, photoId, userAdapter.getUser());
        return ResponseEntity.status(HttpStatus.OK)
            .body("Album cover updated!");
    }
}
