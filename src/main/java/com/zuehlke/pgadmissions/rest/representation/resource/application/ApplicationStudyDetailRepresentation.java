package com.zuehlke.pgadmissions.rest.representation.resource.application;

import org.joda.time.LocalDate;

public class ApplicationStudyDetailRepresentation {

    private String studyLocation;

    private String studyDivision;

    private String studyArea;
    
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
    
    public ApplicationStudyDetailRepresentation withStudyLocation(String studyLocation) {
        this.studyLocation = studyLocation;
        return this;
    }
    
    public ApplicationStudyDetailRepresentation withStudyDivision(String studyDivision) {
        this.studyDivision = studyDivision;
        return this;
    }
    
    public ApplicationStudyDetailRepresentation withStudyArea(String studyArea) {
        this.studyArea = studyArea;
        return this;
    }
    
    public ApplicationStudyDetailRepresentation withStudyApplicationId(String studyApplicationId) {
        this.studyApplicationId = studyApplicationId;
        return this;
    }
    
    public ApplicationStudyDetailRepresentation withStudyStartDate(LocalDate studyStartDate) {
        this.studyStartDate = studyStartDate;
        return this;
    }

}
