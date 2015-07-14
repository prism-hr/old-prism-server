package com.zuehlke.pgadmissions.rest.dto.application;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.joda.time.LocalDate;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityType;
import com.zuehlke.pgadmissions.rest.dto.EntityDTO;

public class ApplicationProgramDetailDTO {

    @NotNull
    private Boolean previousApplication;

    private PrismOpportunityType opportunityType;

    @NotNull
    private EntityDTO studyOption;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private EntityDTO referralSource;

    private List<String> primaryThemes = Lists.newArrayList();

    private List<String> secondaryThemes = Lists.newArrayList();

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

    public EntityDTO getReferralSource() {
        return referralSource;
    }

    public void setReferralSource(EntityDTO referralSource) {
        this.referralSource = referralSource;
    }

    public List<String> getPrimaryThemes() {
        return primaryThemes;
    }

    public void setPrimaryThemes(List<String> primaryThemes) {
        this.primaryThemes = primaryThemes;
    }

    public List<String> getSecondaryThemes() {
        return secondaryThemes;
    }

    public void setSecondaryThemes(List<String> secondaryThemes) {
        this.secondaryThemes = secondaryThemes;
    }

}
