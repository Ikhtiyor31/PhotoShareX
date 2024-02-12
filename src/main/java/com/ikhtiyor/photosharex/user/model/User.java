package com.ikhtiyor.photosharex.user.model;


import com.ikhtiyor.photosharex.AuditableEntity;
import com.ikhtiyor.photosharex.photo.model.Album;
import com.ikhtiyor.photosharex.photo.model.Photo;
import com.ikhtiyor.photosharex.user.dto.UserRegisterRequest;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "users", uniqueConstraints = {@UniqueConstraint(
    name = "user_email_unique", columnNames = "email"
)})
@SQLRestriction("deleted=false")
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
}
