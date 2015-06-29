package com.zuehlke.pgadmissions.rest.representation.resource.application;

import java.math.BigDecimal;
import java.util.List;

import com.zuehlke.pgadmissions.domain.definitions.PrismStudyOption;
import com.zuehlke.pgadmissions.rest.representation.UserRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.advert.AdvertRepresentation;

public class ApplicationClientRepresentation extends ApplicationRepresentation {
    
    private List<PrismStudyOption> availableStudyOptions;
    
    private List<String> possibleThemes;

    private List<String> possibleLocations;
    
    private List<UserRepresentation> usersInterestedInApplication;

    private List<UserRepresentation> usersPotentiallyInterestedInApplication;

    private ApplicationInterviewRepresentation interview;

    private ApplicationOfferRepresentation offerRecommendation;

    private List<ApplicationAssignedSupervisorRepresentation> assignedSupervisors;

    private BigDecimal applicationRatingAverage;

    private ApplicationSummaryRepresentation resourceSummary;

    private List<AdvertRepresentation> recommendedAdverts;

    public List<PrismStudyOption> getAvailableStudyOptions() {
        return availableStudyOptions;
    }

    public void setAvailableStudyOptions(List<PrismStudyOption> availableStudyOptions) {
        this.availableStudyOptions = availableStudyOptions;
    }

    public List<String> getPossibleThemes() {
        return possibleThemes;
    }

    public void setPossibleThemes(List<String> possibleThemes) {
        this.possibleThemes = possibleThemes;
    }

    public List<String> getPossibleLocations() {
        return possibleLocations;
    }

    public void setPossibleLocations(List<String> possibleLocations) {
        this.possibleLocations = possibleLocations;
    }

    public List<UserRepresentation> getUsersInterestedInApplication() {
        return usersInterestedInApplication;
    }

    public void setUsersInterestedInApplication(List<UserRepresentation> usersInterestedInApplication) {
        this.usersInterestedInApplication = usersInterestedInApplication;
    }

    public List<UserRepresentation> getUsersPotentiallyInterestedInApplication() {
        return usersPotentiallyInterestedInApplication;
    }

    public void setUsersPotentiallyInterestedInApplication(List<UserRepresentation> usersPotentiallyInterestedInApplication) {
        this.usersPotentiallyInterestedInApplication = usersPotentiallyInterestedInApplication;
    }

    public ApplicationInterviewRepresentation getInterview() {
        return interview;
    }

    public void setInterview(ApplicationInterviewRepresentation interview) {
        this.interview = interview;
    }

    public ApplicationOfferRepresentation getOfferRecommendation() {
        return offerRecommendation;
    }

    public void setOfferRecommendation(ApplicationOfferRepresentation offerRecommendation) {
        this.offerRecommendation = offerRecommendation;
    }

    public List<ApplicationAssignedSupervisorRepresentation> getAssignedSupervisors() {
        return assignedSupervisors;
    }

    public void setAssignedSupervisors(List<ApplicationAssignedSupervisorRepresentation> assignedSupervisors) {
        this.assignedSupervisors = assignedSupervisors;
    }

    public BigDecimal getApplicationRatingAverage() {
        return applicationRatingAverage;
    }

    public void setApplicationRatingAverage(BigDecimal applicationRatingAverage) {
        this.applicationRatingAverage = applicationRatingAverage;
    }

    public ApplicationSummaryRepresentation getResourceSummary() {
        return resourceSummary;
    }

    public void setResourceSummary(ApplicationSummaryRepresentation resourceSummary) {
        this.resourceSummary = resourceSummary;
    }

    public List<AdvertRepresentation> getRecommendedAdverts() {
        return recommendedAdverts;
    }

    public void setRecommendedAdverts(List<AdvertRepresentation> recommendedAdverts) {
        this.recommendedAdverts = recommendedAdverts;
    }
    
}
