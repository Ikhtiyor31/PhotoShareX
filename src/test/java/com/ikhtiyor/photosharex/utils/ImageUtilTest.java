package com.ikhtiyor.photosharex.utils;


import static org.assertj.core.api.Assertions.assertThat;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

class ImageUtilTest {

    @Test
    void should_check_if_file_type_is_image() throws IOException {
        String imagePath = "src/test/resources/test_image.jpg";
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "image.jpg",
            "image/jpeg",
            getClass().getClassLoader().getResourceAsStream(imagePath)
        );
        boolean success = ImageUtil.isImage(file);
        assertThat(success).isEqualTo(Boolean.TRUE);
    }

    @Test
    void check_file_type_not_image() throws IOException {
        String imagePath = "src/test/resources/test_text.txt";
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "test_text.txt",
            "text/plain",
            getClass().getClassLoader().getResourceAsStream(imagePath)
        );
        boolean isNotImageType = ImageUtil.isImage(file);
        assertThat(isNotImageType).isEqualTo(Boolean.FALSE);
    }

    @Test
    void generate_image_name() {
        String originalImageName = "example.jpg";
        String expectedImageExtension = ".jpg";
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy_MM_dd_HHmmss");
        String expectedTimestamp = currentTime.format(formatter);
        String expectedGeneratedImageName = "example_" + expectedTimestamp + expectedImageExtension;

        String generatedImageName = ImageUtil.generateImageName(originalImageName);

        assertThat(expectedGeneratedImageName).isEqualTo(generatedImageName);
    }
}