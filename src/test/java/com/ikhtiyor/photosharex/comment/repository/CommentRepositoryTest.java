package com.ikhtiyor.photosharex.comment.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.ikhtiyor.photosharex.comment.model.Comment;
import com.ikhtiyor.photosharex.photo.dto.PhotoRequest;
import com.ikhtiyor.photosharex.photo.enums.VisibilityType;
import com.ikhtiyor.photosharex.photo.model.Photo;
import com.ikhtiyor.photosharex.photo.repository.AlbumRepository;
import com.ikhtiyor.photosharex.photo.repository.PhotoRepository;
import com.ikhtiyor.photosharex.user.dto.UserRegisterRequest;
import com.ikhtiyor.photosharex.user.model.User;
import com.ikhtiyor.photosharex.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private PhotoRepository photoRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        albumRepository.deleteAll();
        photoRepository.deleteAll();
        commentRepository.deleteAll();
        userRepository.deleteAll();
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void givenCommentEntity_whenSaveComment_thenReturnsSuccess() {
        // Given
        PhotoRequest photoRequest = new PhotoRequest(
            "http://localhost:8080/image_2024_02_11_23423.jpg",
            "New Image",
            "This is a beautiful image",
            VisibilityType.PUBLIC,
            "Seoul"
        );
        String email = "test1@gmail.com";
        User user = getUser(email);
        Photo photo = Photo.createOf(photoRequest, user);
        Photo savedPhoto = photoRepository.save(photo);
        String commentMessage = "this is my first comment message for shared album";

        Comment comment = Comment.createOf(savedPhoto, user, commentMessage);
        // When
        commentRepository.save(comment);
        // Then
        assertThat(comment.getId()).isGreaterThan(0);
        assertThat(comment.getMessage()).isEqualTo(commentMessage);
        assertThat(comment.getUser().getEmail()).isEqualTo(email);
    }

    @Test
    void givenComment_whenFindByPhoto_IdAndUser_thenReturnsPageOfComments() {
        // Given
        User user = getUser("tourist@gmail.com");
        PhotoRequest photoRequest = new PhotoRequest(
            "http://localhost:8080/image_2024_02_11_23423.jpg",
            "New Image",
            "This is a beautiful image",
            VisibilityType.PUBLIC,
            "Seoul"
        );
        Photo photo = Photo.createOf(photoRequest, user);
        photoRepository.save(photo);
        Comment comment = Comment.createOf(photo, user, "Test comment");
        commentRepository.save(comment);

        Long photoId = photo.getId();
        // When
        Page<Comment> comments = commentRepository.findByPhoto_IdAndUser(photoId, user,
            PageRequest.of(0, 10));

        // Then
        assertTrue(comments.hasContent());
        assertThat(comments.getTotalElements()).isEqualTo(1);
        assertThat(comments.getContent().get(0).getMessage()).isEqualTo("Test comment");

    }

    @Test
    void givenComment_whenDeleteComment_thenSuccess() {
        // Given
        User user = getUser("tourist@gmail.com");
        PhotoRequest photoRequest = new PhotoRequest(
            "http://localhost:8080/image_2024_02_11_23423.jpg",
            "New Image",
            "This is a beautiful image",
            VisibilityType.PUBLIC,
            "Seoul"
        );
        Photo photo = Photo.createOf(photoRequest, user);
        photoRepository.save(photo);
        Comment comment = Comment.createOf(photo, user, "Test comment 1");
        Comment comment2 = Comment.createOf(photo, user, "Test comment 2");
        Comment comment3 = Comment.createOf(photo, user, "Test comment 3");
        commentRepository.save(comment);
        commentRepository.save(comment2);
        commentRepository.save(comment3);

        comment.setDeleted();
        Long commentId = comment.getId();
        boolean commentExists = commentRepository.existsById(commentId);
        Long photoId = photo.getId();
        final var comments = commentRepository.findByPhoto_IdAndUser(
            photoId, user, PageRequest.of(0, 10)
        );

        assertFalse(commentExists);
        assertThat(comments.getTotalElements()).isEqualTo(2);
    }

    private User getUser(String email) {
        UserRegisterRequest request = new UserRegisterRequest(
            "abdul",
            email,
            "aslfasjffasfd",
            ""
        );

        User user = User.createOf(request, "encoasdfafalas");
        return userRepository.save(user);
    }
}