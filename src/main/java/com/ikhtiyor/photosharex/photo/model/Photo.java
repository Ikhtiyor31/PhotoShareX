package com.ikhtiyor.photosharex.photo.model;


import com.ikhtiyor.photosharex.AuditableEntity;
import com.ikhtiyor.photosharex.comment.model.Comment;
import com.ikhtiyor.photosharex.like.model.Like;
import com.ikhtiyor.photosharex.photo.dto.PhotoRequest;
import com.ikhtiyor.photosharex.photo.enums.VisibilityType;
import com.ikhtiyor.photosharex.user.model.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "photos", indexes = {@Index(name = "idx_photo_user_id", columnList = "user_id")})
@SQLRestriction("deleted=false")
@DynamicUpdate
public class Photo extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
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

    @Column(name = "location", nullable = false)
    private String location;

    @OneToMany(
        mappedBy = "photo",
        cascade = {CascadeType.PERSIST, CascadeType.REMOVE},
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(
        mappedBy = "photo",
        cascade = {CascadeType.PERSIST, CascadeType.REMOVE},
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    private List<Like> likes = new ArrayList<>();


    public Photo() {
    }

    public Photo(
        User user,
        String imageUrl,
        String title,
        String description,
        VisibilityType visibilityType,
        String location
    ) {
        this.user = user;
        this.imageUrl = imageUrl;
        this.title = title;
        this.description = description;
        this.visibilityType = visibilityType;
        this.location = location;
    }

    public static Photo createOf(PhotoRequest request, User user) {
        return new Photo(
            user,
            request.imageUrl(),
            request.title(),
            request.description(),
            request.visibilityType(),
            request.location()
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

    public String getLocation() {
        return location;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setVisibilityType(VisibilityType visibilityType) {
        this.visibilityType = visibilityType;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public List<Like> getLikes() {
        return likes;
    }
}
