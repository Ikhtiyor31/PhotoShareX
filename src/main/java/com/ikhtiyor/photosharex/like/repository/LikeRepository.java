package com.ikhtiyor.photosharex.like.repository;

import com.ikhtiyor.photosharex.like.model.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {

}
