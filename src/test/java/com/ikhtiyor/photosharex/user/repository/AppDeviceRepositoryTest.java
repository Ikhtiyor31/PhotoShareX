package com.ikhtiyor.photosharex.user.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.ikhtiyor.photosharex.user.PlatformType;
import com.ikhtiyor.photosharex.user.dto.AppDeviceDTO;
import com.ikhtiyor.photosharex.user.dto.UserRegisterRequest;
import com.ikhtiyor.photosharex.user.model.AppDevice;
import com.ikhtiyor.photosharex.user.model.User;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class AppDeviceRepositoryTest {

    @Autowired
    private AppDeviceRepository appDeviceRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
    }

    @Test
    void shouldSaveAndFindDeviceByDeviceKey() {
        User savedUser = createUser();
        AppDeviceDTO appDeviceDto = new AppDeviceDTO(
            "device-123",
            "fcm-token-123",
            "192.168.1.1",
            PlatformType.ANDROID,
            "1.0.0"
        );

        // Given
        AppDevice device = AppDevice.fromDto(appDeviceDto, savedUser);

        // When
        appDeviceRepository.save(device);
        Optional<AppDevice> foundDevice = appDeviceRepository.findByDeviceKey("device-123");

        // Then
        assertTrue(foundDevice.isPresent());
        assertThat(foundDevice.get().getDeviceFcmToken()).isEqualTo("fcm-token-123");
    }

    @Test
    void shouldUpdateDevice() {
        // Given
        AppDeviceDTO appDeviceDto = new AppDeviceDTO(
            "device-456",
            "old-fcm-token",
            "192.168.1.1",
            PlatformType.IOS,
            "1.0.0"
        );
        User savedUser = createUser();
        AppDevice device = AppDevice.fromDto(appDeviceDto, savedUser);
        appDeviceRepository.save(device);

        // When
        AppDevice savedDevice = appDeviceRepository.findByDeviceKey("device-456").orElseThrow();
        savedDevice.setDeviceFcmToken("new-fcm-token");
        appDeviceRepository.save(savedDevice);

        // Then
        AppDevice updatedDevice = appDeviceRepository.findByDeviceKey("device-456").orElseThrow();
        assertThat(updatedDevice.getDeviceFcmToken()).isEqualTo("new-fcm-token");
    }

    @Test
    void shouldReturnEmptyForNonExistingDeviceKey() {
        // When
        Optional<AppDevice> foundDevice = appDeviceRepository.findByDeviceKey("non-existent-key");

        // Then
        assertThat(foundDevice).isEmpty();
    }

    private User createUser() {
        UserRegisterRequest request = new UserRegisterRequest(
            "test",
            "test@gmail.com",
            "password",
            ""
        );

        User user = User.createOf(request, "myencodedpassword");

        return userRepository.save(user);
    }
}