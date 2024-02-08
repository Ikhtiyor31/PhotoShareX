package com.ikhtiyor.photosharex.user.model;


import com.ikhtiyor.photosharex.AuditableEntity;
import com.ikhtiyor.photosharex.user.dto.UserResgisterRequest;
import jakarta.persistence.*;

@Entity(name = "User")
@Table(name = "users", uniqueConstraints = {@UniqueConstraint(
    name = "user_email_unique", columnNames = "email"
)})
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

    public User() {}
    public User(String name, String email, String password, String profilePhoto) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.profilePhoto = profilePhoto;
        this.roleType = RoleType.USER;
    }

    public static User createOf(UserResgisterRequest request, String encodedPassword) {
        return new User(
            request.getName(),
            request.getEmail(),
            encodedPassword,
            request.getProfilePhoto()
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
}
