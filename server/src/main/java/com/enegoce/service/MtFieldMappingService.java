package com.enegoce.service;


import com.enegoce.entities.EntityField;
import com.enegoce.entities.MtFieldMapping;
import com.enegoce.entities.MtFieldMappingInput;
import com.enegoce.repository.MtFieldMappingRepository;
import jakarta.persistence.EntityNotFoundException;
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
        mtFieldMapping.setFieldName(input.fieldName());
        mtFieldMapping.setFieldDescription(input.fieldDescription());
        mtFieldMapping.setDatabaseField(input.databaseField());
        mtFieldMapping.setEntityName(input.entityName());
        mtFieldMapping.setMt(input.mt());
        mtFieldMapping.setFieldOrder(input.fieldOrder());
        mtFieldMapping.setMappingRule(constructMappingRule(
                input.fields(),
                input.delimiter(),
                input.code()
        ));

        return mappingRepo.save(mtFieldMapping);
    }

    public MtFieldMapping updateMtFieldMapping(Integer id, MtFieldMappingInput input) {
        Optional<MtFieldMapping> existingOpt = mappingRepo.findById(id);

        if (existingOpt.isPresent()) {
            MtFieldMapping existing = existingOpt.get();

            existing.setStatus(input.status());
            existing.setTag(input.tag());
            existing.setFieldName(input.fieldName());
            existing.setFieldDescription(input.fieldDescription());
            existing.setDatabaseField(input.databaseField());
            existing.setEntityName(input.entityName());
            existing.setMt(input.mt());
            existing.setFieldOrder(input.fieldOrder());
            existing.setMappingRule(constructMappingRule(
                    input.fields(),
                    input.delimiter(),
                    input.code()
            ));

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
        boolean hasContent = false;

        mappingRule.append("{");

        if (fields != null && !fields.isEmpty() && fields.stream().anyMatch(field -> !field.isEmpty())) {
            hasContent = true;
            mappingRule.append("\"fields\": [");
            for (int i = 0; i < fields.size(); i++) {
                if (!fields.get(i).isEmpty()) {
                    mappingRule.append("\"").append(fields.get(i)).append("\"");
                    if (i < fields.size() - 1 && !fields.get(i + 1).isEmpty()) {
                        mappingRule.append(",");
                    }
                }
            }
            mappingRule.append("]");
        }

        if (delimiter != null && !delimiter.isEmpty()) {
            if (hasContent) {
                mappingRule.append(",");
            }
            hasContent = true;
            mappingRule.append("\"delimiter\": \"").append(delimiter).append("\"");
        }

        if (code != null && !code.isEmpty()) {
            if (hasContent) {
                mappingRule.append(",");
            }
            hasContent = true;
            mappingRule.append("\"code\": \"").append(code).append("\"");
        }

        mappingRule.append("}");

        return hasContent ? mappingRule.toString() : null;
    }


    public String getMappingRuleById(Integer id) {
        Optional<MtFieldMapping> optionalMapping = mappingRepo.findById(id);
        if (optionalMapping.isPresent()) {
            return optionalMapping.get().getMappingRule();
        } else {
            throw new EntityNotFoundException("MtFieldMapping with id " + id + " not found");
        }
    }

}
