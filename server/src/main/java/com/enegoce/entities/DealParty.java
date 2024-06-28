package com.enegoce.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class DealParty implements Serializable {

    @EmbeddedId
    private DealPartyPK id;

    @Column(length = 35)
    private String nom;
    
    @Column(length = 105)
    private String street1;
    
    @Column(length = 105)
    private String street2;
    
    @Column(length = 105)
    private String street3;
    
    @Column(length = 35)
    private String town;
    
    @Column(length = 35)
    private String cp;
    
    @Column(length = 2)
    private String country;
    
    @Column(length = 35)
    private String iban;
    
    @Column(length = 35)
    private String dev;
    
    @Column(length = 35)
    private String dynamicData1;
    
    @Column(length = 35)
    private String dynamicData2;
    
    @Column(length = 35)
    private String dynamicData3;
    
    @Column(length = 35)
    private String nidn;
    
    @Column(length = 1)
    private String compCheck;
    
    @Column(length = 35)
    private String userCreated;
    
    private LocalDate dateCreation;
    
    @Column(length = 35)
    private String userUpdated;
    
    @Column(length = 35)
    private LocalDate dateUpdated;
    
    @Column(length = 5)
    private String stepId;
}
