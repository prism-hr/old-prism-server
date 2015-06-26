package com.zuehlke.pgadmissions.rest.representation.resource;

import java.util.List;

public class ResourceAttributesRepresentation {

    private List<String> studyLocations;

    public List<String> getStudyLocations() {
        return studyLocations;
    }

    public void setStudyLocations(List<String> studyLocations) {
        this.studyLocations = studyLocations;
    }
    
    public ResourceAttributesRepresentation withStudyLocations(List<String> studyLocations) {
        this.studyLocations = studyLocations;
        return this;
    }

}
