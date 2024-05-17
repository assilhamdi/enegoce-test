package com.enegoce.graphcontroller;

import com.enegoce.entities.MtFieldMapping;
import com.enegoce.entities.MtFieldMappingInput;
import com.enegoce.service.MtMappingService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
public class MtMappingController {

    private static final Logger logger = LogManager.getLogger(DealLCController.class);

    @Autowired
    private MtMappingService service;

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
    public List<String> mts(){ return service.mts();} //For populating Select

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
        return service.mappings();
    }

    @QueryMapping
    public List<MtFieldMapping> orderMappingsByST(@Argument boolean order) {
        return service.mappings();
    }

    @QueryMapping
    public List<MtFieldMapping> orderMappingsByFD(@Argument boolean order) {
        return service.mappings();
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
    public boolean deleteFieldMapping(@Argument Integer id) {
        return service.deleteMtFieldMapping(id);
    }

    @MutationMapping
    public String exportMT(@Argument Integer id, @Argument String mt) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String mt700FilePath = "C:/Users/Assil/IdeaProjects/enegoce/server/src/test/output/MT" + mt + "_" + timestamp + ".txt";

        boolean conversionSuccessful = service.generateAndExportMtMessage(id, mt, mt700FilePath);
        String response;

        if (conversionSuccessful) {
            response = String.valueOf(ResponseEntity.ok()
                    .body("{\"message\": \"Conversion successful\", \"mt700FilePath\": \"" + mt700FilePath + "\"}"));
        } else {
            logger.error("Unsuccessful Conversion");
            response = String.valueOf(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"message\": \"Conversion failed. Please check your Input and try again.\"}"));
        }
        return response;


    }
}
