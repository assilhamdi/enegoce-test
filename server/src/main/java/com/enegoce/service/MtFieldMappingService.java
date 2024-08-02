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
        return mappingRepo.findAll();
    }

    public MtFieldMapping mappingById(Integer id) {
        Optional<MtFieldMapping> mapping = mappingRepo.findById(id);
        return mapping.orElse(null);
    }
    ///////////////////////Filtering methods///////////////////////
    /////////////////////////////////////////////////////////////

    public List<MtFieldMapping> mappingsByMt(String mt) {
        return mappingRepo.findByMt(mt);
    }

    public List<String> mts() {
        return mappingRepo.findDistinctMtValues();
    }

    public List<MtFieldMapping> mappingsByFD(String fieldDescription) {
        return mappingRepo.findByFieldDescriptionContainingIgnoreCase(fieldDescription);
    }

    public List<MtFieldMapping> mappingsByDF(String dbField) {
        return mappingRepo.findByDatabaseFieldContainingIgnoreCase(dbField);
    }

    public List<MtFieldMapping> mappingsByST(char status) {
        return mappingRepo.findByStatus(status);
    }

    ///////////////////////Sorting methods///////////////////////
    /////////////////////////////////////////////////////////////

    public List<MtFieldMapping> orderMappingsByFO(boolean order) {
        if (order) {
            return mappingRepo.findAllByOrderByFieldOrderAsc();
        } else {
            return mappingRepo.findAllByOrderByFieldOrderDesc();
        }
    }

    public List<MtFieldMapping> orderMappingsByDF(boolean order) {
        if (order) {
            return mappingRepo.findAllByOrderByDatabaseFieldAsc();
        } else {
            return mappingRepo.findAllByOrderByDatabaseFieldDesc();
        }
    }

    public List<MtFieldMapping> orderMappingsByST(boolean order) {
        if (order) {
            return mappingRepo.findAllByOrderByStatusAsc();
        } else {
            return mappingRepo.findAllByOrderByStatusDesc();
        }
    }

    public List<MtFieldMapping> orderMappingsByFD(boolean order) {
        if (order) {
            return mappingRepo.findAllByOrderByFieldDescriptionAsc();
        } else {
            return mappingRepo.findAllByOrderByFieldDescriptionDesc();
        }
    }


    /////////////////////////////////////////////////////////////

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
            return true;
        } else {
            throw new EntityNotFoundException("MtFieldMapping with id " + id + " not found");
        }
    }

}
