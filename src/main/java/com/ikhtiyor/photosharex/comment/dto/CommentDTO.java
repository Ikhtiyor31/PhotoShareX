package com.ikhtiyor.photosharex.comment.dto;

import com.ikhtiyor.photosharex.comment.model.Comment;
import java.time.LocalDateTime;

public record CommentDTO(
    Long commentId,
    String message,
    LocalDateTime createdAt
) {

    public static CommentDTO fromEntity(Comment comment) {
        return new CommentDTO(
            comment.getId(),
            comment.getMessage(),
            comment.getCreatedAt()
        );
    }
}
