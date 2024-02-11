package com.ikhtiyor.photosharex.photo.controller;

import com.ikhtiyor.photosharex.annotation.Authenticated;
import com.ikhtiyor.photosharex.photo.dto.PhotoRequest;
import com.ikhtiyor.photosharex.photo.dto.UploadPhotoDTO;
import com.ikhtiyor.photosharex.photo.service.PhotoService;
import com.ikhtiyor.photosharex.security.UserAdapter;
import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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

    @PreAuthorize("hasRole('USER')")
    @PostMapping(path = "/upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<UploadPhotoDTO> uploadImage(@RequestParam("image") MultipartFile image) {
        final var uploadPhotoDTO =  photoService.uploadImage(image);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(uploadPhotoDTO);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping(path = "/download")
    public ResponseEntity<Resource> download(@RequestParam("image_name") String imageName) {
        Resource file = photoService.downloadImage(imageName);
        return ResponseEntity
            .ok()
            .contentType(MediaType.TEXT_PLAIN)
            .header("Content-Disposition", "attachment; filename=" + file.getFilename())
            .body(file);
    }
}
