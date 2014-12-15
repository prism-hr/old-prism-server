package com.zuehlke.pgadmissions.rest.representation.resource.application;

public class ApplicationStudyDetailRepresentation {

    private String studyLocation;

    private String studyDivision;

    private String studyArea;
    
    private String studyApplicationId;

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

}
