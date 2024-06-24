package com.enegoce.repository;

import com.enegoce.entities.DealParty;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

@Repository
public interface DealPartyRepository extends JpaRepository<DealParty, Long> {

    @Query("SELECT dp FROM DealParty dp WHERE dp.id.deal.id = :dealId")
    List<DealParty> findPartiesByDealId(@Param("dealId") Long dealId);

    @Query("SELECT dp FROM DealParty dp WHERE dp.id.deal.id = :dealId AND dp.id.codPrt LIKE :code")
    DealParty findPartyByDealIdAndCode(@Param("dealId") Long dealId, @Param("code") String code);
}
