package com.ikhtiyor.photosharex.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class VerificationCodeGeneratorTest {

    private VerificationCodeGenerator verificationCodeGenerator;

    @BeforeEach
    public void setUp() {
        verificationCodeGenerator = new VerificationCodeGenerator();
    }

    @Test
    void generate_4digit_code() {
        int length = 4;
        String generatedCode = verificationCodeGenerator.generate(length);
        assertNotNull(generatedCode);
        assertEquals(length, generatedCode.length());
        assertTrue(generatedCode.matches("\\d{4}"));
    }

    @Test
    void generate_4digit_5_different_codes() {
        int length = 4;
        int numberOfCodes = 5;
        HashSet<String> hashSet = new HashSet<>();
        for (int i = 0; i < numberOfCodes; i++) {
            String generatedCode = verificationCodeGenerator.generate(length);
            hashSet.add(generatedCode);
        }
        assertFalse(hashSet.isEmpty());
        assertThat(hashSet.size()).isEqualTo(numberOfCodes);
    }

}