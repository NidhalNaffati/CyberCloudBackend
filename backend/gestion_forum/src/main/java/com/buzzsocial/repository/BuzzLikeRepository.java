package com.buzzsocial.repository;

import com.buzzsocial.model.BuzzLike;
import com.buzzsocial.model.BuzzLikeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BuzzLikeRepository extends JpaRepository<BuzzLike, BuzzLikeId> {
    boolean existsByIdUserIdAndIdBuzzId(String userId, String buzzId);
    
    void deleteByIdUserIdAndIdBuzzId(String userId, String buzzId);
    
    @Query("SELECT COUNT(bl) FROM BuzzLike bl WHERE bl.buzz.id = :buzzId")
    long countByBuzzId(String buzzId);
}
