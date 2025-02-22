package com.ikhtiyor.photosharex.photo.model;

import com.ikhtiyor.photosharex.AuditableEntity;
import com.ikhtiyor.photosharex.photo.dto.AlbumRequest;
import com.ikhtiyor.photosharex.user.model.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "albums")
@SQLRestriction("deleted=false")
public class Album extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "user_id",
        nullable = false,
        referencedColumnName = "id",
        foreignKey = @ForeignKey(
            name = "user_album_fk"
        )
    )
    private User user;

    @Column(name = "title", nullable = false, columnDefinition = "TEXT")
    private String title;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "cover_image_url", nullable = false, columnDefinition = "TEXT")
    private String coverImageUrl;

    @Column(name = "is_shared", nullable = false)
    @ColumnDefault("false")
    private Boolean isShared = false;

    @ManyToMany
    @JoinTable(
        name = "album_shared_users",
        joinColumns = @JoinColumn(name = "album_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private final Set<User> sharedUsers = new HashSet<>();

    @OneToMany(
        mappedBy = "album",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    private List<PhotoAlbum> photoAlbums = new ArrayList<>();

    public Album() {
    }

    public Album(User user, String title, String description) {
        this.user = user;
        this.title = title;
        this.description = description;
        this.coverImageUrl = "";
    }

    public static Album of(AlbumRequest albumRequest, User user) {
        return new Album(
            user,
            albumRequest.title(),
            albumRequest.description()
        );
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public User getUser() {
        return user;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void updateCoverImage(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public void setShared() {
        this.isShared = Boolean.TRUE;
    }

    public Boolean isShared() {
        return isShared;
    }

    public Set<User> getSharedUsers() {
        return sharedUsers;
    }


    @Override
    public String toString() {
        return "Album{" +
            "id=" + id +
            ", user=" + user +
            ", title='" + title + '\'' +
            ", description='" + description + '\'' +
            ", coverImageUrl='" + coverImageUrl + '\'' +
            ", isShared=" + isShared +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Album album)) {
            return false;
        }
        return Objects.equals(getId(), album.getId()) && Objects.equals(getUser(),
            album.getUser()) && Objects.equals(getTitle(), album.getTitle())
            && Objects.equals(getDescription(), album.getDescription())
            && Objects.equals(getCoverImageUrl(), album.getCoverImageUrl())
            && Objects.equals(isShared, album.isShared) && Objects.equals(
            getSharedUsers(), album.getSharedUsers());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getUser(), getTitle(), getDescription(), getCoverImageUrl(),
            isShared, getSharedUsers());
    }
}
