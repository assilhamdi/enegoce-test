package com.enegoce.repository;

import com.enegoce.entities.DealComment;
import com.enegoce.entities.InfoDeal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DealCommentRepository extends JpaRepository<DealComment, Long> {

    @Query("SELECT dc FROM DealComment dc WHERE dc.id.deal.id = :dealId")
    List<DealComment> findCommentsByDealId(@Param("dealId") Long dealId);

    @Query("SELECT dc FROM DealComment dc WHERE dc.id.deal.id = :dealId AND dc.typeComt LIKE :type")
    DealComment findCommentByDealAndType(@Param("dealId") Long dealId,@Param("type") String type);

    @Query("SELECT MAX(dc.id.seq) FROM DealComment dc WHERE dc.id.deal = :deal")
    Long findMaxSeqByDeal(@Param("deal") InfoDeal deal);

}
