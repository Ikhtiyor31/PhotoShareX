package com.ikhtiyor.photosharex.comment.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.ikhtiyor.photosharex.comment.model.Comment;
import com.ikhtiyor.photosharex.photo.dto.PhotoRequest;
import com.ikhtiyor.photosharex.photo.enums.VisibilityType;
import com.ikhtiyor.photosharex.photo.model.Photo;
import com.ikhtiyor.photosharex.photo.repository.AlbumRepository;
import com.ikhtiyor.photosharex.photo.repository.PhotoRepository;
import com.ikhtiyor.photosharex.user.dto.UserRegisterRequest;
import com.ikhtiyor.photosharex.user.model.User;
import com.ikhtiyor.photosharex.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

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

    @BeforeEach
    void setUp() {
        albumRepository.deleteAll();
        photoRepository.deleteAll();
        commentRepository.deleteAll();
        userRepository.deleteAll();
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