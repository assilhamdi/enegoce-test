package com.enegoce.entities;

public record MtFieldMappingInput(char status, String tag, String fieldDescription,
                                  String mappingRule, String databaseField, String entityName,
                                  String mt, Integer fieldOrder) {
}
