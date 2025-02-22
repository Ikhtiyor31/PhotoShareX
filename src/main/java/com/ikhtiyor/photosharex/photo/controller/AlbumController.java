package com.ikhtiyor.photosharex.photo.controller;


import com.ikhtiyor.photosharex.annotation.Authenticated;
import com.ikhtiyor.photosharex.photo.dto.AlbumDTO;
import com.ikhtiyor.photosharex.photo.dto.AlbumRequest;
import com.ikhtiyor.photosharex.photo.dto.PhotoDTO;
import com.ikhtiyor.photosharex.photo.dto.PhotoIdsRequest;
import com.ikhtiyor.photosharex.photo.service.AlbumService;
import com.ikhtiyor.photosharex.security.UserAdapter;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
        albumService.createAlbum(albumRequest, userAdapter.user());
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
        final var itemAddedMessage = albumService.addPhotosToAlbum(albumId, request,
            userAdapter.user());
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
        albumService.updateAlbumCoverImage(albumId, photoId, userAdapter.user());
        return ResponseEntity.status(HttpStatus.OK)
            .body("Album cover updated!");
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping
    public ResponseEntity<Page<AlbumDTO>> getMyAlbums(
        @PageableDefault(size = 20) Pageable pageable,
        @Authenticated UserAdapter userAdapter) {
        Page<AlbumDTO> albumDTOS = albumService.getMyAlbums(pageable, userAdapter.user());
        return ResponseEntity.status(HttpStatus.OK)
            .body(albumDTOS);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{albumId}/photos")
    public ResponseEntity<Page<PhotoDTO>> getPhotosByAlbum(
        @PathVariable @Min(value = 1, message = "albumId field cannot be null or empty") Long albumId,
        @PageableDefault(size = 20) Pageable pageable,
        @Authenticated UserAdapter userAdapter
    ) {
        Page<PhotoDTO> photoDTOS = albumService.getPhotosByAlbum(
            pageable,
            albumId,
            userAdapter.user()
        );
        return ResponseEntity.status(HttpStatus.OK)
            .body(photoDTOS);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{albumId}")
    public ResponseEntity<AlbumDTO> getAlbum(
        @PathVariable @Min(value = 1, message = "albumId field cannot be null or empty") Long albumId
    ) {
        return ResponseEntity.ok(albumService.getAlbum(albumId));
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/{albumId}/photos")
    public ResponseEntity<String> removePhotosFromAlbum(
        @PathVariable @Min(value = 1, message = "albumId field cannot be null or empty") Long albumId,
        @RequestBody PhotoIdsRequest request,
        @Authenticated UserAdapter userAdapter
    ) {
        final var itemRemovedMessage = albumService.removePhotosFromAlbum(
            albumId,
            request,
            userAdapter.user()
        );
        return ResponseEntity.status(HttpStatus.OK)
            .body(itemRemovedMessage);
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/{albumId}")
    public ResponseEntity<String> updateAlbum(
        @PathVariable @Min(value = 1, message = "albumId field cannot be null or empty") Long albumId,
        @RequestBody AlbumRequest albumRequest,
        @Authenticated UserAdapter userAdapter
    ) {
        albumService.updateAlbum(albumId, albumRequest, userAdapter.user());
        return ResponseEntity.status(HttpStatus.OK)
            .body("Album updated!");
    }


    @PreAuthorize("hasRole('USER')")
    @PutMapping("/{albumId}/invite/{userId}")
    public ResponseEntity<AlbumDTO> inviteUserToAlbum(
        @PathVariable @Min(value = 1, message = "albumId field cannot be null or empty") Long albumId,
        @PathVariable Long userId,
        @Authenticated UserAdapter userAdapter
    ) {
        AlbumDTO albumDTO = albumService.inviteUser(albumId, userId);
        return ResponseEntity.ok(albumDTO);
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/{albumId}")
    public ResponseEntity<String> deleteAlbum(@PathVariable Long albumId,
        @Authenticated UserAdapter userAdapter) {
        albumService.deleteAlbum(albumId, userAdapter.user());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Album deleted successfully.");
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/shared")
    public ResponseEntity<List<AlbumDTO>> listSharedAlbums(
        @Authenticated UserAdapter userAdapter
    ) {
        return ResponseEntity.ok(albumService.listSharedAlbums(userAdapter.user()));
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/{albumId}/shared-users/{userId}")
    public ResponseEntity<Void> removeUserFromSharedAlbum(
        @PathVariable @Min(value = 1, message = "albumId field cannot be null or empty") Long albumId,
        @PathVariable Long userId,
        @Authenticated UserAdapter userAdapter
    ) {
        albumService.revokeAccess(albumId, userId, userAdapter.user());
        return ResponseEntity.noContent().build();
    }

}
