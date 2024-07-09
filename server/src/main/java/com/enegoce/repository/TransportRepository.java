package com.enegoce.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.enegoce.entities.Transport;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransportRepository extends JpaRepository<Transport, Long> {

    @Query("SELECT t FROM Transport t WHERE t.dealTransport.id = :dealId ORDER BY t.id DESC LIMIT 1")
    Optional<Transport> findLatestTransportByDealId(@Param("dealId") Long dealId);

}
