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

    @QueryMapping
    public MtFieldMapping getMappingById(@Argument Integer id) {
        return service.mappingById(id);
    }

    @MutationMapping
    public MtFieldMapping addMtFieldMapping(@Argument MtFieldMappingInput input) {
        return service.createMtFieldMapping(input);
    }

    @MutationMapping
    public MtFieldMapping updateFieldMapping(@Argument Integer id, @Argument MtFieldMapping input) {
        return service.updateMtFieldMapping(id, input);
    }

    @MutationMapping
    public boolean deleteFieldMapping(@Argument Integer id) {
        return service.deleteMtFieldMapping(id);
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
    List<MtFieldMapping> findByFilter(@Argument String filter) {
        return service.findByFilter(filter);
    }

    @QueryMapping
    List<MtFieldMapping> mappingsByST(@Argument char status) {
        return service.mappingsByST(status);
    }

    //////////////////////Inputs handling////////////////////////

    @QueryMapping
    public List<String> getFieldsForEntity(@Argument String entityName) {
        return service.getFieldsForEntity(entityName);
    }

    @QueryMapping
    public String getMappingRule(@Argument Integer id) {
        return service.getMappingRuleById(id);
    }

    @MutationMapping
    public MtFieldMapping updateMappingRule(@Argument Integer id, @Argument List<String> fields, @Argument String delimiter, @Argument String code) {
        return service.updateMappingRule(id, fields, delimiter, code);
    }

    @MutationMapping
    public boolean deleteMappingRule(@Argument Integer id) {
        return service.deleteMappingRule(id);
    }

}
