package com.enegoce.service;


import com.enegoce.entities.EntityField;
import com.enegoce.entities.MtFieldMapping;
import com.enegoce.entities.MtFieldMappingInput;
import com.enegoce.repository.MtFieldMappingRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MtFieldMappingService {

    @Autowired
    private MtFieldMappingRepository mappingRepo;

    public List<MtFieldMapping> mappings() {
        return mappingRepo.findAllOrderedByMtAndFieldOrder();
    }

    public MtFieldMapping mappingById(Integer id) {
        Optional<MtFieldMapping> mapping = mappingRepo.findById(id);
        return mapping.orElse(null);
    }

    public MtFieldMapping createMtFieldMapping(MtFieldMappingInput input) {
        MtFieldMapping mtFieldMapping = new MtFieldMapping();

        mtFieldMapping.setStatus(input.status());
        mtFieldMapping.setTag(input.tag());
        mtFieldMapping.setFieldDescription(input.fieldDescription());
        mtFieldMapping.setMappingRule(null);
        mtFieldMapping.setDatabaseField(input.databaseField());
        mtFieldMapping.setEntityName(input.entityName());
        mtFieldMapping.setMt(input.mt());
        mtFieldMapping.setFieldOrder(input.fieldOrder());

        return mappingRepo.save(mtFieldMapping);
    }

    public MtFieldMapping updateMtFieldMapping(Integer id, MtFieldMapping input) {
        Optional<MtFieldMapping> existingOpt = mappingRepo.findById(id);

        if (existingOpt.isPresent()) {
            MtFieldMapping existing = existingOpt.get();

            existing.setStatus(input.getStatus());
            existing.setTag(input.getTag());
            existing.setFieldDescription(input.getFieldDescription());
            existing.setMappingRule(input.getMappingRule());
            existing.setDatabaseField(input.getDatabaseField());
            existing.setEntityName(input.getEntityName());
            existing.setMt(input.getMt());
            existing.setFieldOrder(input.getFieldOrder());

            return mappingRepo.save(existing);
        } else {
            throw new EntityNotFoundException("MtFieldMapping with id " + id + " not found");
        }
    }

    public boolean deleteMtFieldMapping(Integer id) {
        // Check if the entity exists before attempting to delete
        if (mappingRepo.findById(id).isPresent()) {
            // Delete the MtFieldMapping by its ID
            mappingRepo.deleteById(id);
            return true;
        } else {
            throw new EntityNotFoundException("MtFieldMapping with id " + id + " not found");
        }
    }

    ///////////////////////Filtering methods///////////////////////
    /////////////////////////////////////////////////////////////

    public List<MtFieldMapping> mappingsByMt(String mt) {
        return mappingRepo.findByMtOrderByFieldOrderAsc(mt);
    }

    public List<String> mts() {
        return mappingRepo.findDistinctMtValues();
    }

    public List<MtFieldMapping> findByFilter(String filter) {
        return mappingRepo.findByFilter(filter);
    }

    public List<MtFieldMapping> mappingsByST(char status) {
        return mappingRepo.findByStatusOrderedByMtAndFieldOrder(status);
    }

    //////////////////////Inputs handling////////////////////////

    public List<String> getFieldsForEntity(String entityName) {
        return Arrays.stream(EntityField.values())
                .filter(e -> e.getEntity().equals(entityName))
                .map(EntityField::getField)
                .collect(Collectors.toList());
    }

    //////////////////////Mapping rule handling////////////////////////

    public String constructMappingRule(List<String> fields, String delimiter, String code) {
        StringBuilder mappingRule = new StringBuilder();

        mappingRule.append("{\"fields\": [");

        for (int i = 0; i < fields.size(); i++) {
            mappingRule.append("\"").append(fields.get(i)).append("\"");
            if (i < fields.size() - 1) {
                mappingRule.append(",");
            }
        }

        mappingRule.append("]");

        if (delimiter != null && !delimiter.isEmpty()) {
            mappingRule.append(",\"delimiter\": \"").append(delimiter).append("\"");
        }

        if (code != null && !code.isEmpty()) {
            mappingRule.append(",\"code\": \"").append(code).append("\"");
        }

        mappingRule.append("}");

        return mappingRule.toString();
    }

    public String getMappingRuleById(Integer id) {
        Optional<MtFieldMapping> optionalMapping = mappingRepo.findById(id);
        if (optionalMapping.isPresent()) {
            return optionalMapping.get().getMappingRule();
        } else {
            throw new EntityNotFoundException("MtFieldMapping with id " + id + " not found");
        }
    }

    public MtFieldMapping updateMappingRule(Integer id, List<String> fields, String delimiter, String code) {
        Optional<MtFieldMapping> optionalMapping = mappingRepo.findById(id);

        if (optionalMapping.isPresent()) {
            MtFieldMapping mtFieldMapping = optionalMapping.get();
            mtFieldMapping.setMappingRule(constructMappingRule(fields, delimiter, code));
            return mappingRepo.save(mtFieldMapping);
        } else {
            throw new EntityNotFoundException("MtFieldMapping with id " + id + " not found");
        }
    }

    public boolean deleteMappingRule(Integer id) {
        Optional<MtFieldMapping> optionalMapping = mappingRepo.findById(id);

        if (optionalMapping.isPresent()) {
            MtFieldMapping mtFieldMapping = optionalMapping.get();
            mtFieldMapping.setMappingRule(null);
            this.updateMtFieldMapping(id, mtFieldMapping);
            return true;
        } else {
            throw new EntityNotFoundException("MtFieldMapping with id " + id + " not found");
        }
    }

}
