package com.enegoce.entities;

import java.util.List;

public record MtFieldMappingInput(char status, String tag, String fieldName,
                                  String fieldDescription, String databaseField,
                                  String entityName, String mt, Integer fieldOrder,
                                  List<String> fields, String delimiter, String code) {
}
