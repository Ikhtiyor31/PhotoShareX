package com.ikhtiyor.photosharex.photo.model;


import com.ikhtiyor.photosharex.AuditableEntity;
import com.ikhtiyor.photosharex.photo.dto.PhotoRequest;
import com.ikhtiyor.photosharex.photo.enums.VisibilityType;
import com.ikhtiyor.photosharex.user.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "photos", indexes = {@Index(name = "idx_photo_user_id", columnList = "user_id")})
public class Photo extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(
        name = "user_id",
        nullable = false,
        referencedColumnName = "id",
        foreignKey = @ForeignKey(
            name = "user_photo_fk"
        )
    )
    private User user;


    @Column(name = "image_url", nullable = false, columnDefinition = "TEXT")
    private String imageUrl;

    @Column(name = "title", nullable = false, columnDefinition = "TEXT")
    private String title;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "visibility_type", nullable = false)
    private VisibilityType visibilityType = VisibilityType.PUBLIC;

    public Photo(){}

    public Photo(User user, String imageUrl, String title, String description, VisibilityType visibilityType) {
        this.user = user;
        this.imageUrl = imageUrl;
        this.title = title;
        this.description = description;
        this.visibilityType = visibilityType;
    }
    public static Photo createOf(PhotoRequest request, User user) {
        return new Photo(
            user,
            request.imageUrl(),
            request.title(),
            request.description(),
            request.visibilityType()
        );
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public VisibilityType getVisibilityType() {
        return visibilityType;
    }
}
