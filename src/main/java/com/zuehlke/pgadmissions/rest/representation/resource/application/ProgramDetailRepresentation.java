package com.zuehlke.pgadmissions.rest.representation.resource.application;

import org.joda.time.LocalDate;

import com.zuehlke.pgadmissions.domain.definitions.PrismStudyOption;

public class ProgramDetailRepresentation {

    private String studyLocation;

    private String studyDivision;

    private String studyArea;

    private PrismStudyOption studyOption;

    private LocalDate startDate;

    private Integer referralSource;

    public final String getStudyLocation() {
        return studyLocation;
    }

    public final void setStudyLocation(String studyLocation) {
        this.studyLocation = studyLocation;
    }

    public final String getStudyDivision() {
        return studyDivision;
    }

    public final void setStudyDivision(String studyDivision) {
        this.studyDivision = studyDivision;
    }

    public final String getStudyArea() {
        return studyArea;
    }

    public final void setStudyArea(String studyArea) {
        this.studyArea = studyArea;
    }

    public PrismStudyOption getStudyOption() {
        return studyOption;
    }

    public void setStudyOption(PrismStudyOption studyOption) {
        this.studyOption = studyOption;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public Integer getReferralSource() {
        return referralSource;
    }

    public void setReferralSource(Integer referralSource) {
        this.referralSource = referralSource;
    }

}
