package com.ikhtiyor.photosharex.photo.controller;

import com.ikhtiyor.photosharex.annotation.Authenticated;
import com.ikhtiyor.photosharex.photo.dto.PhotoDTO;
import com.ikhtiyor.photosharex.photo.dto.PhotoRequest;
import com.ikhtiyor.photosharex.photo.dto.PhotoUpdateRequest;
import com.ikhtiyor.photosharex.photo.dto.UploadPhotoDTO;
import com.ikhtiyor.photosharex.photo.enums.VisibilityType;
import com.ikhtiyor.photosharex.photo.service.PhotoService;
import com.ikhtiyor.photosharex.security.UserAdapter;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/v1/photos")
@Validated
public class PhotoController {

    private final PhotoService photoService;

    public PhotoController(PhotoService photoService) {
        this.photoService = photoService;
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping
    public ResponseEntity<Page<PhotoDTO>> getPhotoList(
        @PageableDefault(size = 20) Pageable pageable,
        @Authenticated UserAdapter userAdapter
    ) {
        Page<PhotoDTO> photoList = photoService.getPhotoList(pageable, userAdapter.getUser());
        return ResponseEntity.status(HttpStatus.OK)
            .body(photoList);

    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PostMapping
    public ResponseEntity<String> createPhoto(
        @RequestBody @Valid PhotoRequest request,
        @Authenticated UserAdapter userAdapter
    ) {
        photoService.createPhoto(request, userAdapter.getUser());
        return ResponseEntity.status(HttpStatus.CREATED)
            .body("photo created!");
    }

    @PreAuthorize("hasRole('USER')")
    @PatchMapping("/{photoId}")
    public ResponseEntity<String> updatePhotoDetail(
        @RequestBody @Valid PhotoUpdateRequest request,
        @PathVariable Long photoId,
        @Authenticated UserAdapter userAdapter
    ) {
        photoService.updatePhotoDetail(request, photoId, userAdapter.getUser());
        return ResponseEntity.status(HttpStatus.OK)
            .body("photo updated!");
    }

    @PreAuthorize("hasRole('USER')")
    @PatchMapping("/{photoId}/change-visibility")
    public ResponseEntity<String> changePhotoVisibility(
        @PathVariable Long photoId,
        @RequestParam("visibility_type") @NotNull(message = "visibility_type field cannot be null or empty") VisibilityType visibilityType,
        @Authenticated UserAdapter userAdapter
    ) {
        photoService.changePhotoVisibility(photoId, visibilityType, userAdapter.getUser());
        return ResponseEntity.status(HttpStatus.OK)
            .body("visibility type updated");
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
