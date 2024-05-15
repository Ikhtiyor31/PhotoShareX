package com.ikhtiyor.photosharex.user.repository;


import com.ikhtiyor.photosharex.user.model.VerificationCode;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {

    Optional<VerificationCode> findTopByEmailOrderByIdDesc(String email);
}
