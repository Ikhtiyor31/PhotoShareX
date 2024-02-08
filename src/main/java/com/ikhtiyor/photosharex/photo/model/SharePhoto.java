package com.ikhtiyor.photosharex.photo.model;

import com.ikhtiyor.photosharex.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "share_photos")
public class SharePhoto extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(
        name = "photo_id",
        nullable = false,
        referencedColumnName = "id",
        foreignKey = @ForeignKey(
            name = "share_photo_id_fk"
        ))
    private Photo photo;

    @Column(name = "shared_by_user_id", nullable = false)
    private Long sharedByUserId;

    @Column(name = "shared_with_user_id", nullable = false)
    private Long sharedWithUserId;

    public SharePhoto() {
    }

    public SharePhoto(Photo photo, Long sharedByUserId, Long sharedWithUserId) {
        this.photo = photo;
        this.sharedByUserId = sharedByUserId;
        this.sharedWithUserId = sharedWithUserId;
    }

    public Long getId() {
        return id;
    }

    public Photo getPhoto() {
        return photo;
    }

    public Long getSharedByUserId() {
        return sharedByUserId;
    }

    public Long getSharedWithUserId() {
        return sharedWithUserId;
    }
}