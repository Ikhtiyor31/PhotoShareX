package com.ikhtiyor.photosharex.user.service;

import com.ikhtiyor.photosharex.user.dto.AppDeviceDTO;
import com.ikhtiyor.photosharex.user.model.AppDevice;
import com.ikhtiyor.photosharex.user.model.User;

public interface AppDeviceService {

    void createDevice(AppDeviceDTO appDeviceDto, User user);

    AppDevice updateDevice(AppDeviceDTO dto);
}
