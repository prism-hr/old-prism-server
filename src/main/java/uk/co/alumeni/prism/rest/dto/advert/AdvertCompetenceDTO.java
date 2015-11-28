package uk.co.alumeni.prism.rest.dto.advert;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Range;

import uk.co.alumeni.prism.rest.dto.TagDTO;

public class AdvertCompetenceDTO extends TagDTO {

    @NotNull
    @Range(min = 1, max = 3)
    private Integer importance;

    public Integer getImportance() {
        return importance;
    }

    public void setImportance(Integer importance) {
        this.importance = importance;
    }

}
