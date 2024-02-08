package com.ikhtiyor.photosharex.photo.controller;

import com.ikhtiyor.photosharex.annotation.Authenticated;
import com.ikhtiyor.photosharex.photo.dto.PhotoRequest;
import com.ikhtiyor.photosharex.photo.service.PhotoService;
import com.ikhtiyor.photosharex.security.UserAdapter;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/photos")
public class PhotoController {

    private final PhotoService photoService;

    public PhotoController(PhotoService photoService) {
        this.photoService = photoService;
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PostMapping
    public ResponseEntity<String> createPhoto(
        @Valid @RequestBody PhotoRequest request,
        @Authenticated UserAdapter userAdapter
    ) {
        photoService.createPhoto(request, userAdapter.getUser());
        return ResponseEntity.status(HttpStatus.CREATED)
            .body("photo created!");
    }

}
