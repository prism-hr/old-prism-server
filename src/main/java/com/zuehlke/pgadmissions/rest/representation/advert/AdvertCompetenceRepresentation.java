package com.zuehlke.pgadmissions.rest.representation.advert;

public class AdvertCompetenceRepresentation extends AdvertTargetRepresentation {

    private String description;

    private Integer importance;

    public String getDescription() {
        return description;
    }

    public Integer getImportance() {
        return importance;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public AdvertCompetenceRepresentation withId(Integer id) {
        setId(id);
        return this;
    }

    public AdvertCompetenceRepresentation withName(String name) {
        setName(name);
        return this;
    }

    public AdvertCompetenceRepresentation withDescription(String description) {
        this.description = description;
        return this;
    }

    public AdvertCompetenceRepresentation withImportance(Integer importance) {
        this.importance = importance;
        return this;
    }

}
