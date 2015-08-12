package com.zuehlke.pgadmissions.rest.dto.advert;

public class AdvertCompetenceDTO extends AdvertTargetDTO {

    private String name;

    private String description;

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
