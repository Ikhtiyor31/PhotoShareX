package com.ikhtiyor.photosharex.comment.repository;


import com.ikhtiyor.photosharex.comment.model.Comment;
import com.ikhtiyor.photosharex.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findByPhoto_IdAndUser(Long photoId, User user, Pageable pageable);
}
