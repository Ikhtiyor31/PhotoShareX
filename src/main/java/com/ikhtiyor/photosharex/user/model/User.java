package com.ikhtiyor.photosharex.user.model;


import com.ikhtiyor.photosharex.AuditableEntity;
import com.ikhtiyor.photosharex.photo.model.Album;
import com.ikhtiyor.photosharex.photo.model.Photo;
import com.ikhtiyor.photosharex.user.dto.UserRegisterRequest;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "users", uniqueConstraints = {@UniqueConstraint(
    name = "user_email_unique", columnNames = "email"
)})
@SQLRestriction("deleted = false")
public class User extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, columnDefinition = "TEXT")
    private String name;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "profile_photo")
    private String profilePhoto;

    @Enumerated(EnumType.STRING)
    @Column(name = "role_type", nullable = false)
    private RoleType roleType;

    @Column(name = "enabled", columnDefinition = "boolean default false")
    private boolean enabled;

    @OneToMany(
        mappedBy = "user",
        cascade = {CascadeType.PERSIST, CascadeType.REMOVE},
        orphanRemoval = true,
        fetch = FetchType.EAGER
    )
    private List<Photo> photos = new ArrayList<>();

    @OneToMany(
        mappedBy = "user",
        cascade = {CascadeType.PERSIST, CascadeType.REMOVE},
        orphanRemoval = true,
        fetch = FetchType.EAGER
    )
    private List<Album> albums = new ArrayList<>();

    public User() {
    }

    public User(String name, String email, String password, String profilePhoto) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.profilePhoto = profilePhoto;
        this.roleType = RoleType.USER;
    }

    public static User createOf(UserRegisterRequest request, String encodedPassword) {
        return new User(
            request.name(),
            request.email(),
            encodedPassword,
            request.profilePhoto()
        );
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public RoleType getRoleType() {
        return roleType;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }

    public List<Photo> getPhotos() {
        return photos;
    }

    public void addPhoto(Photo photo) {
        photos.add(photo);
    }

    public void addAlbum(Album album) {
        albums.add(album);
    }

    public List<Album> getAlbums() {
        return albums;
    }

    public void setUserId(Long userId) {
        this.id = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "User{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", email='" + email + '\'' +
            ", password='" + password + '\'' +
            ", profilePhoto='" + profilePhoto + '\'' +
            ", roleType=" + roleType +
            ", enabled=" + enabled +
            '}';
    }
}
