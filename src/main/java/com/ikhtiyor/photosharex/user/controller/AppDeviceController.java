package com.ikhtiyor.photosharex.user.controller;

import com.ikhtiyor.photosharex.annotation.Authenticated;
import com.ikhtiyor.photosharex.security.UserAdapter;
import com.ikhtiyor.photosharex.user.dto.AppDeviceDTO;
import com.ikhtiyor.photosharex.user.model.AppDevice;
import com.ikhtiyor.photosharex.user.service.AppDeviceService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/devices")
public class AppDeviceController {

    private final AppDeviceService appDeviceService;

    public AppDeviceController(AppDeviceService appDeviceService) {
        this.appDeviceService = appDeviceService;
    }

    @PostMapping
    public ResponseEntity<String> createDevice(
        @Valid @RequestBody AppDeviceDTO dto,
        @Authenticated UserAdapter userAdapter
    ) {
        appDeviceService.createDevice(dto, userAdapter.user());
        return ResponseEntity.ok("Created device");
    }


    @PutMapping
    public ResponseEntity<AppDevice> updateDevice(@Valid @RequestBody AppDeviceDTO dto) {
        return ResponseEntity.ok(appDeviceService.updateDevice(dto));
    }
}
