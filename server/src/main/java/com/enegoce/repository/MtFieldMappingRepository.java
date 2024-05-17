package com.enegoce.repository;

import com.enegoce.entities.MtFieldMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MtFieldMappingRepository extends JpaRepository<MtFieldMapping, Integer> {

    ///////////////////////Filtering methods///////////////////////
    /////////////////////////////////////////////////////////////
    List<MtFieldMapping> findByMt(String mt); //Mt Lookup

    @Query("SELECT DISTINCT m.mt FROM MtFieldMapping m")
    List<String> findDistinctMtValues();

    List<MtFieldMapping> findByFieldDescriptionContaining(String fieldDescription); //Field description lookup

    List<MtFieldMapping> findByDatabaseFieldContaining(String fieldDescription); //Field description lookup

    List<MtFieldMapping> findByStatus(char status); //Field description lookup

    ///////////////////////Sorting methods///////////////////////
    /////////////////////////////////////////////////////////////

    List<MtFieldMapping> findAllByOrderByFieldOrderAsc();

    List<MtFieldMapping> findAllByOrderByFieldOrderDesc();

    List<MtFieldMapping> findAllByOrderByDatabaseFieldAsc();

    List<MtFieldMapping> findAllByOrderByDatabaseFieldDesc();

    List<MtFieldMapping> findAllByOrderByStatusAsc();

    List<MtFieldMapping> findAllByOrderByStatusDesc();

    List<MtFieldMapping> findAllByOrderByFieldDescriptionAsc();

    List<MtFieldMapping> findAllByOrderByFieldDescriptionDesc();


}
