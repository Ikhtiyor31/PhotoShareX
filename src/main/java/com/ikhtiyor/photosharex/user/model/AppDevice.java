package com.ikhtiyor.photosharex.user.model;


import com.ikhtiyor.photosharex.AuditableEntity;
import com.ikhtiyor.photosharex.user.PlatformType;
import com.ikhtiyor.photosharex.user.dto.AppDeviceDTO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "app_devices")
@SQLRestriction("deleted=false")
public class AppDevice extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "device_key", nullable = false, unique = true)
    private String deviceKey;

    @Column(name = "device_fcm_token", nullable = false)
    private String deviceFcmToken;

    @Column(name = "device_ip_address")
    private String deviceIpAddress;

    @Enumerated(EnumType.STRING)
    @Column(name = "platform_type", nullable = false)
    private PlatformType platformType = PlatformType.UNKNOWN;

    @Column(name = "app_version", nullable = false)
    private String appVersion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "user_id",
        nullable = false,
        referencedColumnName = "id",
        foreignKey = @ForeignKey(
            name = "user_device_fk"
        )
    )
    private User user;

    public static AppDevice fromDto(AppDeviceDTO dto, User user) {
        AppDevice appDevice = new AppDevice();
        appDevice.deviceKey = dto.deviceKey();
        appDevice.deviceFcmToken = dto.deviceFcmToken();
        appDevice.platformType = dto.platformType();
        appDevice.appVersion = dto.appVersion();
        appDevice.user = user;
        return appDevice;
    }

    public void updateFromDto(AppDeviceDTO dto) {
        this.deviceFcmToken = dto.deviceFcmToken();
        this.deviceIpAddress = dto.deviceIpAddress();
        this.platformType = dto.platformType();
        this.appVersion = dto.appVersion();
    }

    public Long getId() {
        return id;
    }

    public String getDeviceKey() {
        return deviceKey;
    }

    public String getDeviceFcmToken() {
        return deviceFcmToken;
    }

    public void setDeviceFcmToken(String fcmToken) {
        this.deviceFcmToken = fcmToken;
    }

    public String getDeviceIpAddress() {
        return deviceIpAddress;
    }

    public PlatformType getPlatformType() {
        return platformType;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public User getUser() {
        return user;
    }
}
