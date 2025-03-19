package com.ikhtiyor.photosharex.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ikhtiyor.photosharex.user.PlatformType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AppDeviceDTO(
    @NotBlank(message = "Device key is required")
    @JsonProperty("device_key")
    String deviceKey,

    @NotBlank(message = "FCM token is required")
    @JsonProperty("device_fcm_token")
    String deviceFcmToken,

    @JsonProperty("device_ip_address")
    String deviceIpAddress,

    @NotNull(message = "Platform type is required")
    @JsonProperty("platform_type")
    PlatformType platformType,

    @NotBlank(message = "App version is required")
    @JsonProperty("app_version")
    String appVersion

) {

}
