package com.zuehlke.pgadmissions.rest.representation.resource;

import java.util.List;

public class ResourceAttributesRepresentation {

    private List<ResourceConditionRepresentation> resourceConditions;

    private List<String> studyLocations;

    public List<ResourceConditionRepresentation> getResourceConditions() {
        return resourceConditions;
    }

    public void setResourceConditions(List<ResourceConditionRepresentation> resourceConditions) {
        this.resourceConditions = resourceConditions;
    }

    public List<String> getStudyLocations() {
        return studyLocations;
    }

    public void setStudyLocations(List<String> studyLocations) {
        this.studyLocations = studyLocations;
    }
}
