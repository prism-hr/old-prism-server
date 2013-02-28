package com.zuehlke.pgadmissions.dto;

import java.util.Date;

public class ConfirmSupervisionDTO {

    private Boolean confirmedSupervision;

    private String declinedSupervisionReason;

    private String projectTitle;

    private String projectAbstract;

    private Date recommendedStartDate;

    private Boolean recommendedConditionsAvailable;

    private String recommendedConditions;

    public Boolean getConfirmedSupervision() {
        return confirmedSupervision;
    }

    public void setConfirmedSupervision(Boolean confirmedSupervision) {
        this.confirmedSupervision = confirmedSupervision;
    }

    public String getDeclinedSupervisionReason() {
        return declinedSupervisionReason;
    }

    public void setDeclinedSupervisionReason(String declinedSupervisionReason) {
        this.declinedSupervisionReason = declinedSupervisionReason;
    }

    public String getProjectTitle() {
        return projectTitle;
    }

    public void setProjectTitle(String projectTitle) {
        this.projectTitle = projectTitle;
    }

    public String getProjectAbstract() {
        return projectAbstract;
    }

    public void setProjectAbstract(String projectAbstract) {
        this.projectAbstract = projectAbstract;
    }

    public Date getRecommendedStartDate() {
        return recommendedStartDate;
    }

    public void setRecommendedStartDate(Date recommendedStartDate) {
        this.recommendedStartDate = recommendedStartDate;
    }

    public Boolean getRecommendedConditionsAvailable() {
        return recommendedConditionsAvailable;
    }

    public void setRecommendedConditionsAvailable(Boolean recommendedConditionsAvailable) {
        this.recommendedConditionsAvailable = recommendedConditionsAvailable;
    }

    public String getRecommendedConditions() {
        return recommendedConditions;
    }

    public void setRecommendedConditions(String recommendedConditions) {
        this.recommendedConditions = recommendedConditions;
    }

}