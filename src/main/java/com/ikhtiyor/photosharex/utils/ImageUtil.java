package com.ikhtiyor.photosharex.utils;

import com.ikhtiyor.photosharex.exception.InvalidImageException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

public final class ImageUtil {
    private static final Integer IMAGE_WIDTH = 600;
    private static final Integer IMAGE_HEIGHT = 600;
    public static boolean isImage(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && contentType.contains("image");
    }

    public static String generateImageName(String originalImageName) {
        String imageExtension = FilenameUtils.getExtension(originalImageName);
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy_MM_dd_HHmmss");
        String timestamp = currentTime.format(formatter);
        String baseImageName = FilenameUtils.removeExtension(originalImageName);
        String generatedFileName = baseImageName.replaceAll("[^a-zA-Z0-9.-]", "_") + "_" + timestamp;

        if (!imageExtension.isEmpty())
            generatedFileName += "." + imageExtension;

        return generatedFileName;
    }

    public static ByteArrayOutputStream resizeImage(InputStream inputStream) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Thumbnails.of(inputStream)
                .size(IMAGE_WIDTH, IMAGE_HEIGHT)
                .outputQuality(1.0f)
                .toOutputStream(outputStream);
            return outputStream;
        } catch (IOException e) {
            throw new InvalidImageException("Failed to resize image: " + e.getMessage());
        }
    }
}
