package com.zuehlke.pgadmissions.domain.application;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class ApplicationStudyDetail {

    @Column(name = "study_location")
    private String studyLocation;

    @Column(name = "study_division")
    private String studyDivision;

    @Column(name = "study_area")
    private String studyArea;
    
    @Column(name = "study_application_id")
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
    
    public ApplicationStudyDetail withStudyLocation(String studyLocation) {
        this.studyLocation = studyLocation;
        return this;
    }

    public ApplicationStudyDetail withStudyDivision(String studyDivision) {
        this.studyDivision = studyDivision;
        return this;
    }

    public ApplicationStudyDetail withStudyArea(String studyArea) {
        this.studyArea = studyArea;
        return this;
    }
    
    public ApplicationStudyDetail withStudyApplicationId(String studyApplicationId) {
        this.studyApplicationId = studyApplicationId;
        return this;
    }
    
}
