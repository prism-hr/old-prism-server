package com.zuehlke.pgadmissions.rest.dto.advert;

public class AdvertCompetenceDTO extends AdvertTargetDTO {

    private String title;
    
    private String description;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
}
