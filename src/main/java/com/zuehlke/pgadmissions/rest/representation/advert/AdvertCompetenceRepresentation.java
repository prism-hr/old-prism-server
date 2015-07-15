package com.zuehlke.pgadmissions.rest.representation.advert;

import java.math.BigDecimal;

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
    
    public AdvertCompetenceRepresentation withTitle(String title) {
        setTitle(title);
        return this;
    }
    
    public AdvertCompetenceRepresentation withDescription(String description) {
        setDescription(description);
        return this;
    }
    
    public AdvertCompetenceRepresentation withImportance(BigDecimal importance) {
        setImportance(importance);
        return this;
    }
    
}
