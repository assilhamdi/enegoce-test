package com.enegoce.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "mt_field_mapping")
public class MtFieldMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "CHAR(1) DEFAULT 'O'")
    private char status;

    @Column(name = "Tag")
    private String tag;

    private String fieldDescription;

    private String mappingRule;

    private String databaseField;

    private String entityName;

    private String mt;

    private Integer fieldOrder;


    public MtFieldMapping() {
    }

    public MtFieldMapping(Integer id, char status, String tag, String fieldDescription, String mappingRule, String databaseField, String entityName, String mt, Integer fieldOrder) {
        this.id = id;
        this.status = status;
        this.tag = tag;
        this.fieldDescription = fieldDescription;
        this.mappingRule = mappingRule;
        this.databaseField = databaseField;
        this.entityName = entityName;
        this.mt = mt;
        this.fieldOrder = fieldOrder;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public char getStatus() {
        return status;
    }

    public void setStatus(char status) {
        this.status = status;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getFieldDescription() {
        return fieldDescription;
    }

    public void setFieldDescription(String fieldDescription) {
        this.fieldDescription = fieldDescription;
    }

    public String getMappingRule() {
        return mappingRule;
    }

    public void setMappingRule(String mappingRule) {
        this.mappingRule = mappingRule;
    }

    public String getDatabaseField() {
        return databaseField;
    }

    public void setDatabaseField(String databaseField) {
        this.databaseField = databaseField;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getMt() {
        return mt;
    }

    public void setMt(String mt) {
        this.mt = mt;
    }

    public Integer getFieldOrder() {
        return fieldOrder;
    }

    public void setFieldOrder(Integer order) {
        this.fieldOrder = order;
    }
}
