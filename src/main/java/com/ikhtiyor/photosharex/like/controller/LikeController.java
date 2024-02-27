package com.ikhtiyor.photosharex.like.controller;

import com.ikhtiyor.photosharex.annotation.Authenticated;
import com.ikhtiyor.photosharex.like.service.LikeService;
import com.ikhtiyor.photosharex.security.UserAdapter;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/likes")
public class LikeController {

    private final LikeService likeService;

    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/albums/{albumId}/photos/{photoId}")
    public ResponseEntity<Void> createLike(
        @PathVariable @Min(value = 1, message = "albumId field must be an positive") Long albumId,
        @PathVariable @Min(value = 1, message = "photoId field must be an positive number") Long photoId,
        @Authenticated UserAdapter userAdapter
    ) {
        likeService.createLike(albumId, photoId, userAdapter.getUser());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/{likeId}")
    public ResponseEntity<String> removeLike(
        @PathVariable @Min(value = 1, message = "commentId field must be an positive number") Long likeId
    ) {
        likeService.removeLike(likeId);
        return ResponseEntity.status(HttpStatus.OK)
            .body("like removed!");
    }
}
