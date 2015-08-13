package com.zuehlke.pgadmissions.rest.dto.advert;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class AdvertCompetenceDTO {

    @Size(max = 255)
    private String name;

    @Size(max = 2000)
    private String description;

    @NotNull
    private Integer importance;

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

}
