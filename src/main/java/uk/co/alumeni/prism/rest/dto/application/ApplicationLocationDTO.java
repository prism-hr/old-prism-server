package uk.co.alumeni.prism.rest.dto.application;

import uk.co.alumeni.prism.rest.dto.resource.ResourceRelationDTO;

public class ApplicationLocationDTO {

    private ResourceRelationDTO resource;

    private String description;

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

    public Boolean getPreference() {
        return preference;
    }

    public void setPreference(Boolean preference) {
        this.preference = preference;
    }

}
