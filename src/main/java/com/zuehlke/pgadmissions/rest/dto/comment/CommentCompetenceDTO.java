package com.zuehlke.pgadmissions.rest.dto.comment;

import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

public class CommentCompetenceDTO {

    @NotNull
    private Integer competenceId;

    @NotNull
    @Range(min = 1, max = 3)
    private Integer importance;

    @NotNull
    private Integer rating;

    private String remark;

    public Integer getCompetenceId() {
        return competenceId;
    }

    public void setCompetenceId(Integer competenceId) {
        this.competenceId = competenceId;
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
