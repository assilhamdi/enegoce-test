package com.engoce.deal.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DealCommentDto {
    private DealCommentPKID id;

    private String comment;

    private String typeComt;

}
