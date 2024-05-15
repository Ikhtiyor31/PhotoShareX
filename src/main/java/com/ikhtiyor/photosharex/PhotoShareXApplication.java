package com.ikhtiyor.photosharex;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class PhotoShareXApplication {

    public static void main(String[] args) {
        SpringApplication.run(PhotoShareXApplication.class, args);
    }

}
