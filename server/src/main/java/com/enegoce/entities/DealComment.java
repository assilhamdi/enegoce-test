package com.enegoce.entities;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;


@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class DealComment implements Serializable {

    @EmbeddedId
    private DealCommentPK id;

    @Column(length = 6500)
    private String comment;

    private LocalDate dateCreation;

    @Column(length = 35)
    private String useName;

    @Column(length = 5)
    private String typeComt;

    private String stepId;
}
