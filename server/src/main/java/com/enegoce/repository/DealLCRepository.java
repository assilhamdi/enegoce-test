package com.enegoce.repository;

import com.enegoce.entities.DealLC;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DealLCRepository extends JpaRepository<DealLC,Integer> {
}
