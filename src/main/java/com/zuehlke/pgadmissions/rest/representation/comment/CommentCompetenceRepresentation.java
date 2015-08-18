package com.zuehlke.pgadmissions.rest.representation.comment;

import javax.validation.constraints.NotNull;

public class CommentCompetenceRepresentation {

    @NotNull
    private Integer competence;

    private String name;

    private String description;

    @NotNull
    private Integer importance;

    @NotNull
    private Integer rating;

    private String remark;

    public Integer getCompetence() {
        return competence;
    }

    public void setCompetence(Integer competence) {
        this.competence = competence;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getImportance() {
        return importance;
    }

    public void setImportance(Integer importance) {
        this.importance = importance;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public CommentCompetenceRepresentation withCompetence(Integer competence) {
        this.competence = competence;
        return this;
    }

    public CommentCompetenceRepresentation withName(String name) {
        this.name = name;
        return this;
    }

    public CommentCompetenceRepresentation withDescription(String description) {
        this.description = description;
        return this;
    }

    public CommentCompetenceRepresentation withImportance(Integer importance) {
        this.importance = importance;
        return this;
    }

    public CommentCompetenceRepresentation withRating(Integer rating) {
        this.rating = rating;
        return this;
    }

    public CommentCompetenceRepresentation withRemark(String remark) {
        this.remark = remark;
        return this;
    }

}
