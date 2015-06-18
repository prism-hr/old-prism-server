package com.zuehlke.pgadmissions.rest.dto.advert;

import javax.validation.constraints.NotNull;

import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityType;

public class AdvertSearchDTO {

    @NotNull
    private PrismOpportunityType opportunityType;

    private String keywords;

    private String location;

    private String institutionId;

    private String[] studyOptions;

    public final PrismOpportunityType getOpportunityType() {
        return opportunityType;
    }

    public final void setOpportunityType(PrismOpportunityType opportunityType) {
        this.opportunityType = opportunityType;
    }

    public final String getKeywords() {
        return keywords;
    }

    public final void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public final String getLocation() {
        return location;
    }

    public final void setLocation(String location) {
        this.location = location;
    }

    public final String getInstitutionId() {
        return institutionId;
    }

    public final void setInstitutionId(String institutionId) {
        this.institutionId = institutionId;
    }

    public final String[] getStudyOptions() {
        return studyOptions;
    }

    public final void setStudyOptions(String[] studyOptions) {
        this.studyOptions = studyOptions;
    }

}
