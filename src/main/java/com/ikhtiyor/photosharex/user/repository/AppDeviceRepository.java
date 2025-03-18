package com.ikhtiyor.photosharex.user.repository;

import com.ikhtiyor.photosharex.user.model.AppDevice;
import com.ikhtiyor.photosharex.user.model.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppDeviceRepository extends JpaRepository<AppDevice, Long> {

    Optional<AppDevice> findByDeviceKey(String deviceKey);

    List<AppDevice> findAppDeviceByUser(User user);
}
