package com.ikhtiyor.photosharex.comment.repository;


import com.ikhtiyor.photosharex.comment.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

}
