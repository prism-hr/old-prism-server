package uk.co.alumeni.prism.rest.dto.comment;

import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class CommentCompetenceDTO {

    @NotNull
    private Integer competenceId;

    @NotNull
    @Range(min = 1, max = 3)
    private Integer importance;

    private Boolean fulfil;

    private Integer rating;

    @Size(max = 2000)
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

    public Boolean getFulfil() {
        return fulfil;
    }

    public void setFulfil(Boolean fulfil) {
        this.fulfil = fulfil;
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
