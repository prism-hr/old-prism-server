package com.zuehlke.pgadmissions.rest.representation.resource;

import com.zuehlke.pgadmissions.domain.definitions.PrismStudyOption;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCondition;

import java.util.List;

public class ResourceAttributesRepresentation {

    private List<PrismActionCondition> resourceConditions;

    private List<PrismStudyOption> studyOptions;

    private List<String> studyLocations;

    public List<PrismActionCondition> getResourceConditions() {
        return resourceConditions;
    }

    public void setResourceConditions(List<PrismActionCondition> resourceConditions) {
        this.resourceConditions = resourceConditions;
    }

    public List<PrismStudyOption> getStudyOptions() {
        return studyOptions;
    }

    public void setStudyOptions(List<PrismStudyOption> studyOptions) {
        this.studyOptions = studyOptions;
    }

    public List<String> getStudyLocations() {
        return studyLocations;
    }

    public void setStudyLocations(List<String> studyLocations) {
        this.studyLocations = studyLocations;
    }
}
