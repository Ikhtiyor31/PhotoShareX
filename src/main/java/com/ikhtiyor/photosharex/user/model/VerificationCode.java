package com.ikhtiyor.photosharex.user.model;


import com.ikhtiyor.photosharex.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "verification_code")
@SQLRestriction("deleted=false")
public class VerificationCode extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "expiration_time", nullable = false)
    private LocalDateTime expirationTime;

    @Column(name = "verification_time")
    private LocalDateTime verificationTime;


    public VerificationCode() {}

    public VerificationCode(String verificationCode, LocalDateTime expirationTime, String email) {
        this.code = verificationCode;
        this.email = email;
        this.expirationTime = expirationTime;
    }

    public String getCode() {
        return code;
    }

    public LocalDateTime getExpirationTime() {
        return expirationTime;
    }

    public String getEmail() {
        return email;
    }

    public LocalDateTime getVerificationTime() {
        return verificationTime;
    }

    public void setVerificationTime(LocalDateTime verificationTime) {
        this.verificationTime = verificationTime;
    }

    public Boolean isExpired(){
        return expirationTime.isBefore(LocalDateTime.now());
    }
}
