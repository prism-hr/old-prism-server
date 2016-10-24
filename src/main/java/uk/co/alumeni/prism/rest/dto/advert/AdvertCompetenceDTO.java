package uk.co.alumeni.prism.rest.dto.advert;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Range;

import uk.co.alumeni.prism.domain.definitions.PrismCompetenceMode;
import uk.co.alumeni.prism.rest.dto.TagDTO;

public class AdvertCompetenceDTO extends TagDTO {

    @Size(max = 2000)
    private String description;

    @NotNull
    @Range(min = 1, max = 3)
    private Integer importance;

    @NotNull
    private PrismCompetenceMode mode;

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

    public PrismCompetenceMode getMode() {
        return mode;
    }

    public void setMode(PrismCompetenceMode mode) {
        this.mode = mode;
    }

}
