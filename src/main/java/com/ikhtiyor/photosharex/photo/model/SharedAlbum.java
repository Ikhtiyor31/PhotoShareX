package com.ikhtiyor.photosharex.photo.model;

import com.ikhtiyor.photosharex.AuditableEntity;
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
@Table(name = "shared_albums")
@SQLRestriction("deleted=false")
public class SharedAlbum extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(
        name = "album_id",
        nullable = false,
        referencedColumnName = "id",
        foreignKey = @ForeignKey(
            name = "shared_album_id_fk"
        ))
    private Album album;

    @ManyToOne
    @JoinColumn(
        name = "shared_user_id",
        nullable = false,
        referencedColumnName = "id",
        foreignKey = @ForeignKey(
            name = "shared_album_user_id_fk"
        )
    )
    private User sharedUser;

    public SharedAlbum() {
    }

    public SharedAlbum(Album album, User sharedUser) {
        this.album = album;
        this.sharedUser = sharedUser;
    }

    public static SharedAlbum createOf(Album album, User sharedUser) {
        return new SharedAlbum(album, sharedUser);
    }

    public Long getId() {
        return id;
    }

    public Album getAlbum() {
        return album;
    }

    public User getSharedUser() {
        return sharedUser;
    }
}