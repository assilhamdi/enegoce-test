package com.enegoce.repository;

import com.enegoce.entities.DealComment;
import com.enegoce.entities.DealParty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DealCommentRepository extends JpaRepository<DealComment, Integer> {

    @Query("SELECT dc FROM DealComment dc WHERE dc.id.deal.id = :dealId")
    List<DealComment> findCommentsByDealId(@Param("dealId") Integer dealId);

    @Query("SELECT dc FROM DealComment dc WHERE dc.id.deal.id = :dealId AND dc.typeComt LIKE :type")
    DealComment findCommentByDealAndType(@Param("dealId") Integer dealId,@Param("type") String type);

}
