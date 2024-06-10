package com.enegoce.repository;

import com.enegoce.entities.Settlement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SettlementRepository extends JpaRepository<Settlement, Integer> {

    @Query("SELECT s FROM Settlement s WHERE s.deal.id = :dealId")
    List<Settlement> findSettlementsByDealId(@Param("dealId") Integer dealId);

}
