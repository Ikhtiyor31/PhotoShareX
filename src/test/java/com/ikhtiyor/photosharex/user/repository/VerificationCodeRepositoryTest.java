package com.ikhtiyor.photosharex.user.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.ikhtiyor.photosharex.user.model.VerificationCode;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;


@DataJpaTest
class VerificationCodeRepositoryTest {

    @Autowired
    private VerificationCodeRepository verificationCodeRepository;

    @BeforeEach
    void setUp() {
        verificationCodeRepository.deleteAll();
    }

    @Test
    void givenVerificationCodeEntity_whenSave_returnSuccess() {
        String code = "1234";
        String email = "ikhtiyor@gmail.com";
        VerificationCode verificationCode = new VerificationCode(
            code, LocalDateTime.now().plusMinutes(1),
            email
        );

        var savedVerificationCode = verificationCodeRepository.save(verificationCode);
        assertThat(savedVerificationCode).isNotNull();
        assertThat(savedVerificationCode.getCode()).isEqualTo(code);
        assertThat(savedVerificationCode.getEmail()).isEqualTo(email);
    }

    @Test
    void givenVerificationCode_findTopByEmail_thenReturnSuccess() {
        String code = "1234";
        String email = "ikhtiyor@gmail.com";
        VerificationCode verificationCode = new VerificationCode(
            code, LocalDateTime.now().plusMinutes(1),
            email
        );

        verificationCodeRepository.save(verificationCode);

        var foundVerificationCode = verificationCodeRepository.findTopByEmailOrderByIdDesc(email);
        assertTrue(foundVerificationCode.isPresent());
        assertThat(foundVerificationCode.get().getEmail()).isEqualTo(email);
        assertNull(foundVerificationCode.get().getVerificationTime());
    }

    @Test
    void givenVerificationCode_whenManyCodesOfSingleUser_thenReturnTopOne() {
        // Given
        String code1 = "5566";
        String code2 = "7788";
        String code3 = "2233";
        String email = "ikhtiyor@gmail.com";
        VerificationCode verificationCode1 = new VerificationCode(
            code1, LocalDateTime.now().plusMinutes(1),
            email
        );
        VerificationCode verificationCode2 = new VerificationCode(
            code2, LocalDateTime.now().plusMinutes(1),
            email
        );
        VerificationCode verificationCode3 = new VerificationCode(
            code3, LocalDateTime.now().plusMinutes(1),
            email
        );

        // When
        verificationCodeRepository.save(verificationCode1);
        verificationCodeRepository.save(verificationCode2);
        verificationCodeRepository.save(verificationCode3);

        // Then
        var foundTopVerificationCode = verificationCodeRepository.findTopByEmailOrderByIdDesc(email);
        assertTrue(foundTopVerificationCode.isPresent());
        assertThat(foundTopVerificationCode.get().getCode()).isEqualTo(code3);

    }

    @Test
    void givenVerificationEntity_findTopByEmail_whenEmailNotExist_thenFail() {
        String fakeEmail = "fake@gmail.com";
        var foundVerificationCode = verificationCodeRepository.findTopByEmailOrderByIdDesc(fakeEmail);
        assertFalse(foundVerificationCode.isPresent());
    }
}