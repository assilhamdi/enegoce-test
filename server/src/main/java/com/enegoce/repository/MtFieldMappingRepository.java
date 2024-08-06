package com.enegoce.repository;

import com.enegoce.entities.MtFieldMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MtFieldMappingRepository extends JpaRepository<MtFieldMapping, Integer> {

    @Query("SELECT m FROM MtFieldMapping m ORDER BY m.mt ASC, m.fieldOrder ASC")
    List<MtFieldMapping> findAllOrderedByMtAndFieldOrder(); //Get all in order

    ///////////////////////Filtering methods/////////////////////
    /////////////////////////////////////////////////////////////

    @Query("SELECT m FROM MtFieldMapping m WHERE m.mt = :mt ORDER BY m.fieldOrder ASC")
    List<MtFieldMapping> findByMtOrderByFieldOrderAsc(@Param("mt") String mt);

    @Query("SELECT DISTINCT m.mt FROM MtFieldMapping m")
    List<String> findDistinctMtValues(); //For select

    @Query("SELECT m FROM MtFieldMapping m " +
            "WHERE LOWER(m.tag) LIKE LOWER(CONCAT('%', :filter, '%')) " +
            "OR LOWER(m.fieldDescription) LIKE LOWER(CONCAT('%', :filter, '%')) " +
            "OR LOWER(m.entityName) LIKE LOWER(CONCAT('%', :filter, '%')) " +
            "OR LOWER(m.databaseField) LIKE LOWER(CONCAT('%', :filter, '%')) " +
            "ORDER BY m.mt ASC, m.fieldOrder ASC")
    List<MtFieldMapping> findByFilter(@Param("filter") String filter);

    @Query("SELECT m FROM MtFieldMapping m WHERE m.status = :status ORDER BY m.mt ASC, m.fieldOrder ASC")
    List<MtFieldMapping> findByStatusOrderedByMtAndFieldOrder(@Param("status") char status);


}
