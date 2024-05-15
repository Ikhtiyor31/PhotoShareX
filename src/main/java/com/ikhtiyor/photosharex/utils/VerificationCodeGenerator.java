package com.ikhtiyor.photosharex.utils;

import java.util.Random;
import org.springframework.stereotype.Component;

@Component
public class VerificationCodeGenerator {

    private static final Random random = new Random();

    public String generate(int length) {
        if (length != 4) {
            throw new IllegalArgumentException("the verification code length must be 4 digits");
        }

        int randomNumberOrigin = (int) Math.pow(10, length);
        int randomNumberBound = (int) Math.pow(10, length + 1d);

        String authNumber = random.ints(randomNumberOrigin, randomNumberBound)
            .findFirst()
            .orElseThrow(
                () -> new IllegalStateException("failed to generate a verification code")) + "";

        return authNumber.substring(1);
    }
}
