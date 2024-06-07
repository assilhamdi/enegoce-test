package com.enegoce.repository;

import com.enegoce.entities.DealParty;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface DealPartyRepository extends JpaRepository<DealParty,Integer> {
}
