package com.ikhtiyor.photosharex.user.service;

import com.ikhtiyor.photosharex.exception.DeviceAlreadyExistException;
import com.ikhtiyor.photosharex.user.dto.AppDeviceDTO;
import com.ikhtiyor.photosharex.user.model.AppDevice;
import com.ikhtiyor.photosharex.user.model.User;
import com.ikhtiyor.photosharex.user.repository.AppDeviceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class AppDeviceServiceImpl implements AppDeviceService {

    private final AppDeviceRepository appDeviceRepository;

    public AppDeviceServiceImpl(AppDeviceRepository appDeviceRepository) {
        this.appDeviceRepository = appDeviceRepository;
    }

    @Override
    public void createDevice(AppDeviceDTO appDeviceDto, User user) {
        if (appDeviceRepository.findByDeviceKey(appDeviceDto.deviceKey()).isPresent()) {
            throw new DeviceAlreadyExistException("Device already registered");
        }

        AppDevice appDevice = AppDevice.fromDto(appDeviceDto, user);
        appDeviceRepository.save(appDevice);
    }

    @Override
    public AppDevice updateDevice(AppDeviceDTO dto) {
        AppDevice device = appDeviceRepository.findByDeviceKey(dto.deviceKey())
            .orElseThrow(() -> new IllegalArgumentException("Device not found"));

        device.updateFromDto(dto);

        return appDeviceRepository.save(device);
    }
}
