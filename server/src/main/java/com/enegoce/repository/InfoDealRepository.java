package com.enegoce.repository;

import com.enegoce.entities.InfoDeal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InfoDealRepository extends JpaRepository<InfoDeal, Integer> {
}
