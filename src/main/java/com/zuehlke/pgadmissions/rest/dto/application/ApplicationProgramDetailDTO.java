package com.zuehlke.pgadmissions.rest.dto.application;

import javax.validation.constraints.NotNull;

import org.joda.time.LocalDate;

import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityCategory;
import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityType;
import com.zuehlke.pgadmissions.rest.dto.EntityDTO;

public class ApplicationProgramDetailDTO {

    @NotNull
    private Boolean previousApplication;

    private PrismOpportunityType opportunityType;

    private PrismOpportunityCategory opportunityCategory;

    @NotNull
    private EntityDTO studyOption;

    @NotNull
    private LocalDate startDate;

    public Boolean getPreviousApplication() {
        return previousApplication;
    }

    public void setPreviousApplication(Boolean previousApplication) {
        this.previousApplication = previousApplication;
    }

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

    public EntityDTO getStudyOption() {
        return studyOption;
    }

    public void setStudyOption(EntityDTO studyOption) {
        this.studyOption = studyOption;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

}
