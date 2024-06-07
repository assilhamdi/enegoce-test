package com.enegoce.repository;

import com.enegoce.entities.DealGoods;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DealGoodsRepository extends JpaRepository<DealGoods,Integer> {

    @Query("SELECT dg FROM DealGoods dg WHERE dg.deal.id = :dealId")
    List<DealGoods> findGoodsByDealId(@Param("dealId") Integer dealId);

}
