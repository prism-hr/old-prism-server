package uk.co.alumeni.prism.rest.representation.resource.application;

import org.joda.time.DateTime;

import uk.co.alumeni.prism.rest.representation.resource.ResourceRepresentationRelation;

public class ApplicationLocationRepresentation extends ApplicationSectionRepresentation {

    private Integer id;

    private ResourceRepresentationRelation resource;

    private String description;

    private Boolean preference;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ResourceRepresentationRelation getResource() {
        return resource;
    }

    public void setResource(ResourceRepresentationRelation resource) {
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

    public ApplicationLocationRepresentation withId(Integer id) {
        this.id = id;
        return this;
    }

    public ApplicationLocationRepresentation withResource(ResourceRepresentationRelation resource) {
        this.resource = resource;
        return this;
    }

    public ApplicationLocationRepresentation withDescription(String description) {
        this.description = description;
        return this;
    }

    public ApplicationLocationRepresentation withPreference(Boolean preference) {
        this.preference = preference;
        return this;
    }

    public ApplicationLocationRepresentation withLastUpdateTimestamp(DateTime lastUpdatedTimestamp) {
        setLastUpdatedTimestamp(lastUpdatedTimestamp);
        return this;
    }

}
