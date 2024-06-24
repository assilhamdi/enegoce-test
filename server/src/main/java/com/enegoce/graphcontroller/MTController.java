package com.enegoce.graphcontroller;

import com.enegoce.service.MTService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
public class MTController {

    @Autowired
    private MTService mtService;
    private static final Logger logger = LogManager.getLogger(DealController.class);
    private final String userHome = System.getenv("USERPROFILE"); //Temporary for local export
    private final String basePath = userHome + "/IdeaProjects/enegoce/server/src/test/output"; //Temporary for local export

    @MutationMapping
    public String exportMT(@Argument Long id, @Argument String mt, @Argument String format) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String mtFilePath;

        if ("xml".equalsIgnoreCase(format)) {
            mtFilePath = basePath + "/MT" + mt + "_" + timestamp + ".xml";
        } else {
            mtFilePath = basePath + "/MT" + mt + "_" + timestamp + ".txt";
        }

        boolean conversionSuccessful = mtService.generateAndExportMtMessage(id, mt, mtFilePath, "xml".equalsIgnoreCase(format));

        if (conversionSuccessful) {
            String response = String.format("{\"message\": \"Conversion successful\", \"filePath\": \"%s\"}", mtFilePath);
            return ResponseEntity.ok(response).getBody();
        } else {
            String response = "{\"message\": \"Conversion failed. Please check your Input and try again.\"}";
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response).getBody();
        }
    }

    @MutationMapping
    public String exportMT798(@Argument Long dealId, @Argument String mt, @Argument String format) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String mtFilePath;
        String response = null;

        if ("txt".equalsIgnoreCase(format)) {
            mtFilePath = basePath + "/MT798" + "_With_" + mt + "_" + timestamp + ".txt";
        } else if ("xml".equalsIgnoreCase(format)) {
            mtFilePath = basePath + "/MT798" + "_With_" + mt + "_" + timestamp + ".xml";
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response).getBody();
        }

        boolean conversionSuccessful = mtService.generateAndExportMt798Message(dealId, mt, mtFilePath, format);

        if (conversionSuccessful) {
            response = String.format("{\"message\": \"Conversion successful\", \"filePath\": \"%s\"}", mtFilePath);
            return ResponseEntity.ok(response).getBody();
        } else {
            logger.error("Unsuccessful Conversion");
            response = "{\"message\": \"Conversion failed. Please check your Input and try again.\"}";
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response).getBody();
        }
    }

    @MutationMapping
    public String convertTextToXml(@Argument String filePath) {

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String outputFilePath;

        outputFilePath = basePath + "/XML_" + timestamp + ".xml";

        File textFile = new File(filePath);
        if (!textFile.exists() || !textFile.isFile()) {
            return "Invalid file path: " + filePath;
        }

        try {
            File outputFile = mtService.convertTextToXml(textFile, outputFilePath);
            return "XML file successfully generated at: " + outputFile.getAbsolutePath();
        } catch (IOException e) {
            return "Error generating XML file: " + e.getMessage();
        }
    }
}
