package com.zuehlke.pgadmissions.rest.dto.comment;

import javax.validation.constraints.NotNull;

public class CommentCompetenceDTO {

    @NotNull
    private Integer competence;
    
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
    
}
