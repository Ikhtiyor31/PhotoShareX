package com.ikhtiyor.photosharex.comment.model;

import com.ikhtiyor.photosharex.AuditableEntity;
import com.ikhtiyor.photosharex.photo.model.Photo;
import com.ikhtiyor.photosharex.user.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "comments")
@SQLRestriction("deleted=false")
public class Comment extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(
        name = "photo_id",
        nullable = false,
        referencedColumnName = "id",
        foreignKey = @ForeignKey(
            name = "comment_photo_id_fk"
        )
    )
    private Photo photo;

    @ManyToOne
    @JoinColumn(
        name = "user_id",
        nullable = false,
        referencedColumnName = "id",
        foreignKey = @ForeignKey(
            name = "comment_user_id_fk"
        )
    )
    private User user;

    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;

    public Comment() {
    }

    public Comment(Photo photo, User user, String message) {
        this.photo = photo;
        this.user = user;
        this.message = message;
    }

    public Long getId() {
        return id;
    }

    public Photo getPhoto() {
        return photo;
    }

    public User getUser() {
        return user;
    }

    public String getMessage() {
        return message;
    }
}