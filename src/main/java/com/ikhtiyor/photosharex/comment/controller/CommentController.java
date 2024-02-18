package com.ikhtiyor.photosharex.comment.controller;

import com.ikhtiyor.photosharex.annotation.Authenticated;
import com.ikhtiyor.photosharex.comment.service.CommentService;
import com.ikhtiyor.photosharex.security.UserAdapter;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/albums/{albumId}/photos/{photoId}")
    public ResponseEntity<Void> createComment(
        @PathVariable @Min(value = 1, message = "albumId field must be an positive") Long albumId,
        @PathVariable @Min(value = 1, message = "photoId field must be an positive number") Long photoId,
        @RequestParam @NotBlank(message = "Message must not be empty or blank") String message,
        @Authenticated UserAdapter userAdapter
    ) {
        commentService.createComment(albumId, photoId, userAdapter.getUser(), message);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

}
