package com.zuehlke.pgadmissions.rest.representation.advert;

public class AdvertCompetenceRepresentation {

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

    public AdvertCompetenceRepresentation withName(final String name) {
        this.name = name;
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
