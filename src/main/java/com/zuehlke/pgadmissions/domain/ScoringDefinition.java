package com.zuehlke.pgadmissions.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;

import com.zuehlke.pgadmissions.domain.enums.ScoringStage;

@Entity(name = "SCORING_DEFINITION")
public class ScoringDefinition implements Serializable {

    private static final long serialVersionUID = -8059348702240864331L;

    @Id
    @GeneratedValue
    private Integer id;

    @Column(name = "stage")
    @Enumerated(EnumType.STRING)
    private ScoringStage stage;

    @Lob
    @Column(name = "content")
    private String content;

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public ScoringStage getStage() {
        return stage;
    }

    public void setStage(ScoringStage stage) {
        this.stage = stage;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}