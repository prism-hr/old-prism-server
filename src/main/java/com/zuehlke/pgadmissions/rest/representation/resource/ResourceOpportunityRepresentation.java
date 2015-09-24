package com.zuehlke.pgadmissions.rest.representation.resource;

import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityCategory;
import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityType;
import uk.co.alumeni.prism.api.model.imported.response.ImportedEntityResponse;

import java.util.List;

public class ResourceOpportunityRepresentation extends ResourceParentDivisionRepresentation {

    private PrismOpportunityType opportunityType;

    private PrismOpportunityCategory opportunityCategory;

    private List<ImportedEntityResponse> studyOptions;

    private Integer durationMinimum;

    private Integer durationMaximum;

    public PrismOpportunityType getOpportunityType() {
        return opportunityType;
    }

    public void setOpportunityType(PrismOpportunityType opportunityType) {
        this.opportunityType = opportunityType;
    }

    public PrismOpportunityCategory getOpportunityCategory() {
        return opportunityCategory;
    }

    public void setOpportunityCategory(PrismOpportunityCategory opportunityCategory) {
        this.opportunityCategory = opportunityCategory;
    }

    public List<ImportedEntityResponse> getStudyOptions() {
        return studyOptions;
    }

    public void setStudyOptions(List<ImportedEntityResponse> studyOptions) {
        this.studyOptions = studyOptions;
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
