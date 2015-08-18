package com.zuehlke.pgadmissions.rest.representation.resource;

import java.util.List;

import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityType;

import uk.co.alumeni.prism.api.model.imported.response.ImportedEntityResponse;

public class ResourceOpportunityRepresentation extends ResourceParentDivisionRepresentation {

    private PrismOpportunityType opportunityType;

    private List<ImportedEntityResponse> studyOptions;

    private List<String> studyLocations;

    private Integer durationMinimum;

    private Integer durationMaximum;

    public PrismOpportunityType getOpportunityType() {
        return opportunityType;
    }

    public void setOpportunityType(PrismOpportunityType opportunityType) {
        this.opportunityType = opportunityType;
    }

    public List<ImportedEntityResponse> getStudyOptions() {
        return studyOptions;
    }

    public void setStudyOptions(List<ImportedEntityResponse> studyOptions) {
        this.studyOptions = studyOptions;
    }

    public List<String> getStudyLocations() {
        return studyLocations;
    }

    public void setStudyLocations(List<String> studyLocations) {
        this.studyLocations = studyLocations;
    }

    public Integer getDurationMinimum() {
        return durationMinimum;
    }

    public void setDurationMinimum(Integer durationMinimum) {
        this.durationMinimum = durationMinimum;
    }

    public Integer getDurationMaximum() {
        return durationMaximum;
    }

    public void setDurationMaximum(Integer durationMaximum) {
        this.durationMaximum = durationMaximum;
    }

}
