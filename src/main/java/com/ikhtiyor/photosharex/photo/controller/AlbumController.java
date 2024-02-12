package com.ikhtiyor.photosharex.photo.controller;


import com.ikhtiyor.photosharex.annotation.Authenticated;
import com.ikhtiyor.photosharex.photo.dto.AlbumRequest;
import com.ikhtiyor.photosharex.photo.service.AlbumService;
import com.ikhtiyor.photosharex.security.UserAdapter;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
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
        albumService.createAlbum(albumRequest, userAdapter.getUser());
        return ResponseEntity.status(HttpStatus.CREATED)
            .body("Album created!");
    }
}
