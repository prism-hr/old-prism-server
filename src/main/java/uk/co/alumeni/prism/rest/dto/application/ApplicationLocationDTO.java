package uk.co.alumeni.prism.rest.dto.application;

import org.joda.time.LocalDate;

import uk.co.alumeni.prism.rest.dto.resource.ResourceRelationDTO;

public class ApplicationLocationDTO {

    private ResourceRelationDTO resource;

    private String description;

    private LocalDate descriptionDate;

    private Boolean preference;

    public ResourceRelationDTO getResource() {
        return resource;
    }

    public void setResource(ResourceRelationDTO resource) {
        this.resource = resource;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDescriptionDate() {
        return descriptionDate;
    }

    public void setDescriptionDate(LocalDate descriptionDate) {
        this.descriptionDate = descriptionDate;
    }

    public Boolean getPreference() {
        return preference;
    }

    public void setPreference(Boolean preference) {
        this.preference = preference;
    }

}
