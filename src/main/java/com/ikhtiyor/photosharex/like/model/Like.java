package com.ikhtiyor.photosharex.like.model;

import com.ikhtiyor.photosharex.AuditableEntity;
import com.ikhtiyor.photosharex.photo.model.Photo;
import com.ikhtiyor.photosharex.user.model.User;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "likes")
public class Like extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(
        name = "photo_id",
        nullable = false,
        referencedColumnName = "id",
        foreignKey = @ForeignKey(
            name = "like_photo_id_fk"
        )
    )
    private Photo photo;

    @ManyToOne
    @JoinColumn(
        name = "user_id",
        nullable = false,
        referencedColumnName = "id",
        foreignKey = @ForeignKey(
            name = "like_user_id_fk"
        )
    )
    private User user;

    public Like() {}
    public Like(Photo photo, User user) {
        this.photo = photo;
        this.user = user;
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
}
