package com.enegoce.service;


import com.enegoce.entities.MtFieldMapping;
import com.enegoce.entities.MtFieldMappingInput;
import com.enegoce.repository.MtFieldMappingRepository;
import jakarta.persistence.EntityNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class MtMappingService {

    private static final Logger logger = LogManager.getLogger(DealLCService.class);

    @Autowired
    private MtFieldMappingRepository mappingRepo;

    @Autowired
    private DealLCService dealService;

    public List<MtFieldMapping> mappings() {
        return mappingRepo.findAll();
    }

    ///////////////////////Filtering methods///////////////////////
    /////////////////////////////////////////////////////////////

    public List<MtFieldMapping> mappingsByMt(String mt) {
        return mappingRepo.findByMt(mt);
    }

    public List<String> mts (){ return mappingRepo.findDistinctMtValues();}

    public List<MtFieldMapping> mappingsByFD(String fieldDescription) {
        return mappingRepo.findByFieldDescriptionContaining(fieldDescription);
    }

    public List<MtFieldMapping> mappingsByDF(String dbField) {
        return mappingRepo.findByDatabaseFieldContaining(dbField);
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
        mtFieldMapping.setMappingRule(input.mappingRule());
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

    public boolean generateAndExportMtMessage(Integer dealId, String mt, String filePath) {


        Object entity;

        if ("700".equals(mt)) {
            entity = dealService.dealById(dealId);
        } /*else if ("701".equals(mt)) {
            entity = dealGoodsService.dealGoodsById(dealId); //TODO: dealGoodsService
        }*/ else {
            logger.error("Unsupported MT: " + mt);
            return false;
        }

        if (entity == null) {
            logger.error("Entity not found for id: " + dealId);
            return false;
        }

        List<MtFieldMapping> mappings = mappingRepo.findByMt(mt);
        if (mappings.isEmpty()) {
            logger.error("No mappings found for mt: " + mt);
            return false;
        }

        mappings.sort(Comparator.comparingInt(MtFieldMapping::getFieldOrder));
        logger.info(mappings);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {

            for (MtFieldMapping mapping : mappings) {
                String entityName = mapping.getEntityName();
                String fieldName = mapping.getDatabaseField();
                String mt700Tag = mapping.getTag();

                // Find corresponding getter method for the field
                String getterMethodName = "get" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
                logger.info(getterMethodName);
                Object fieldValue = null;
                try {
                    // Dynamically invoke getter method based on entity name
                    if ("DealLC".equals(entityName)) {
                        fieldValue = entity.getClass().getMethod(getterMethodName).invoke(entity);
                    } /*else if ("OtherEntity".equals(entityName)) {
                        // Fetch field value from other entity based on entity name
                    }*/
                } catch (Exception e) {
                    // Handle exception if getter method not found or other issues
                    logger.error("Error accessing field " + fieldName + " in entity " + entityName, e);
                }

                if (fieldValue != null) {
                    // Write MT700 message to text file
                    writer.write(mt700Tag + ":" + fieldValue + "\r\n");
                }
            }
            return true; // Return true if writing is successful
        } catch (Exception e) {
            logger.error("Error accessing file or database: " + e);
            return false; // Return false if accessing file or database fails
        }
    }

}
