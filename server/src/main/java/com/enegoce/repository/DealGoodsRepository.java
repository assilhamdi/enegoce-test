package com.enegoce.repository;

import com.enegoce.entities.DealGoods;
import com.enegoce.entities.InfoDeal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DealGoodsRepository extends JpaRepository<DealGoods, Long> {

    @Query("SELECT dg FROM DealGoods dg WHERE dg.id.deal.id = :dealId")
    List<DealGoods> findGoodsByDealId(@Param("dealId") Long dealId);

    @Query("SELECT MAX(dg.id.seq) FROM DealGoods dg WHERE dg.id.deal = :deal")
    Long findMaxSeqByDeal(@Param("deal") InfoDeal deal);


}
