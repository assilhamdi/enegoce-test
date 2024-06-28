package com.engoce.deal.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DealPartyDto {

    private DealPartyPKID id;
    private String nom;
    private String street1;
    private String street2;
    private String street3;
    private String town;
    private String country;

}
