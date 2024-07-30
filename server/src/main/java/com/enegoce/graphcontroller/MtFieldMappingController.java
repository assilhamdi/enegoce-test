package com.enegoce.graphcontroller;

import com.enegoce.entities.MtFieldMapping;
import com.enegoce.entities.MtFieldMappingInput;
import com.enegoce.service.MtFieldMappingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class MtFieldMappingController {

    @Autowired
    private MtFieldMappingService service;

    @QueryMapping
    public List<MtFieldMapping> getAllMappings() {
        return service.mappings();
    }

    ///////////////////////Filtering methods///////////////////////
    /////////////////////////////////////////////////////////////

    @QueryMapping
    public List<MtFieldMapping> mappingsByMt(@Argument String mt) {
        return service.mappingsByMt(mt);
    }

    @QueryMapping
    public List<String> mts() {
        return service.mts();
    } //For populating Select

    @QueryMapping
    List<MtFieldMapping> mappingsByFD(@Argument String fieldDescription) {
        return service.mappingsByFD(fieldDescription);
    }

    @QueryMapping
    List<MtFieldMapping> mappingsByDF(@Argument String dbField) {
        return service.mappingsByDF(dbField);
    }

    @QueryMapping
    List<MtFieldMapping> mappingsByST(@Argument char status) {
        return service.mappingsByST(status);
    }

    ///////////////////////Sorting methods///////////////////////
    /////////////////////////////////////////////////////////////

    @QueryMapping
    public List<MtFieldMapping> orderMappingsByFO(@Argument boolean order) {
        return service.orderMappingsByFO(order);
    }

    @QueryMapping
    public List<MtFieldMapping> orderMappingsByDF(@Argument boolean order) {
        return service.orderMappingsByDF(order);
    }

    @QueryMapping
    public List<MtFieldMapping> orderMappingsByST(@Argument boolean order) {
        return service.orderMappingsByST(order);
    }

    @QueryMapping
    public List<MtFieldMapping> orderMappingsByFD(@Argument boolean order) {
        return service.orderMappingsByFD(order);
    }

    /////////////////////////////////////////////////////////////

    @MutationMapping
    public MtFieldMapping addMtFieldMapping(@Argument MtFieldMappingInput input) {
        return service.createMtFieldMapping(input);
    }

    @MutationMapping
    public MtFieldMapping updateFieldMapping(@Argument Integer id, @Argument MtFieldMapping input) {
        return service.updateMtFieldMapping(id, input);
    }

    @MutationMapping
    public MtFieldMapping updateMappingRule(@Argument Integer id, @Argument List<String> fields, @Argument String delimiter, @Argument String code) {
        return service.updateMappingRule(id, fields, delimiter, code);
    }

    @MutationMapping
    public boolean deleteFieldMapping(@Argument Integer id) {
        return service.deleteMtFieldMapping(id);
    }

    //////////////////////Inputs handling////////////////////////

    @QueryMapping
    public List<String> getFieldsForEntity(@Argument String entityName) {
        return service.getFieldsForEntity(entityName);
    }


}
