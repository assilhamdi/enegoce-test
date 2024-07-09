package com.enegoce.repository;

import com.enegoce.entities.Settlment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SettlementRepository extends JpaRepository<Settlment, Long> {

    @Query("SELECT s FROM Settlment s WHERE s.deal.id = :dealId ORDER BY s.id DESC LIMIT 1")
    Optional <Settlment> findLatestSettlementByDealId(@Param("dealId") Long dealId);


}
