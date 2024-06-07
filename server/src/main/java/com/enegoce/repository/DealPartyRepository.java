package com.enegoce.repository;

import com.enegoce.entities.DealGoods;
import com.enegoce.entities.DealParty;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

@Repository
public interface DealPartyRepository extends JpaRepository<DealParty,Integer> {

    @Query("SELECT dp FROM DealParty dp WHERE dp.deal.id = :dealId")
    List<DealParty> findPartiesByDealId(@Param("dealId") Integer dealId);
}
