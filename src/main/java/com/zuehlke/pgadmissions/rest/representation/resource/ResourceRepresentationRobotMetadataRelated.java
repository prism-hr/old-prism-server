package com.zuehlke.pgadmissions.rest.representation.resource;

import java.util.List;

public class ResourceRepresentationRobotMetadataRelated {

    private String label;

    private List<ResourceRepresentationIdentity> resources;

    public String getLabel() {
        return label;
    }

    public void setLabel(String relatedResourcesLabel) {
        this.label = relatedResourcesLabel;
    }

    public List<ResourceRepresentationIdentity> getResources() {
        return resources;
    }

    public void setResources(List<ResourceRepresentationIdentity> resources) {
        this.resources = resources;
    }

    public ResourceRepresentationRobotMetadataRelated withLabel(String label) {
        this.label = label;
        return this;
    }

    public ResourceRepresentationRobotMetadataRelated withResources(List<ResourceRepresentationIdentity> resources) {
        this.resources = resources;
        return this;
    }

}
