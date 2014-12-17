package com.zuehlke.pgadmissions.rest.dto.application;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.joda.time.LocalDate;

public class ApplicationStudyDetailDTO {

    @NotNull
    @Size(max = 255)
    private String studyLocation;

    @NotNull
    @Size(max = 255)
    private String studyDivision;

    @NotNull
    @Size(max = 255)
    private String studyArea;
    
    @Size(max = 255)
    private String studyApplicationId;
    
    private LocalDate studyStartDate;

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

    public final String getStudyApplicationId() {
        return studyApplicationId;
    }

    public final void setStudyApplicationId(String studyApplicationId) {
        this.studyApplicationId = studyApplicationId;
    }

    public final LocalDate getStudyStartDate() {
        return studyStartDate;
    }

    public final void setStudyStartDate(LocalDate studyStartDate) {
        this.studyStartDate = studyStartDate;
    }

}
