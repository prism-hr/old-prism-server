package com.zuehlke.pgadmissions.rest.representation.advert;

public class AdvertCompetenceRepresentation extends AdvertTargetRepresentation {

    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public AdvertCompetenceRepresentation withId(Integer id) {
        setId(id);
        return this;
    }

    public AdvertCompetenceRepresentation withName(String title) {
        setName(title);
        return this;
    }

    public AdvertCompetenceRepresentation withDescription(String description) {
        setDescription(description);
        return this;
    }

}
