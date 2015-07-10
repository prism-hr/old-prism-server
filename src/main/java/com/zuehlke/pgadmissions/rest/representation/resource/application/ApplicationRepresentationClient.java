package com.zuehlke.pgadmissions.rest.representation.resource.application;

import java.util.List;

import com.zuehlke.pgadmissions.domain.definitions.PrismStudyOption;
import com.zuehlke.pgadmissions.rest.representation.advert.AdvertRepresentationExtended;
import com.zuehlke.pgadmissions.rest.representation.user.UserRepresentationSimple;

public class ApplicationRepresentationClient extends ApplicationRepresentation {

    private List<String> possibleThemes;

    private List<PrismStudyOption> possibleStudyOptions;

    private List<String> possibleLocations;

    private List<UserRepresentationSimple> usersInterestedInApplication;

    private List<UserRepresentationSimple> usersPotentiallyInterestedInApplication;

    private ApplicationInterviewRepresentation interview;

    private ApplicationOfferRepresentation offerRecommendation;

    private List<ApplicationAssignedSupervisorRepresentation> assignedSupervisors;

    private ApplicationSummaryRepresentation resourceSummary;

    private List<AdvertRepresentationExtended> recommendedAdverts;

    public List<String> getPossibleThemes() {
        return possibleThemes;
    }

    public void setPossibleThemes(List<String> possibleThemes) {
        this.possibleThemes = possibleThemes;
    }

    public List<PrismStudyOption> getPossibleStudyOptions() {
        return possibleStudyOptions;
    }

    public void setPossibleStudyOptions(List<PrismStudyOption> possibleStudyOptions) {
        this.possibleStudyOptions = possibleStudyOptions;
    }

    public List<String> getPossibleLocations() {
        return possibleLocations;
    }

    public void setPossibleLocations(List<String> possibleLocations) {
        this.possibleLocations = possibleLocations;
    }

    public List<UserRepresentationSimple> getUsersInterestedInApplication() {
        return usersInterestedInApplication;
    }

    public void setUsersInterestedInApplication(List<UserRepresentationSimple> usersInterestedInApplication) {
        this.usersInterestedInApplication = usersInterestedInApplication;
    }

    public List<UserRepresentationSimple> getUsersPotentiallyInterestedInApplication() {
        return usersPotentiallyInterestedInApplication;
    }

    public void setUsersPotentiallyInterestedInApplication(List<UserRepresentationSimple> usersPotentiallyInterestedInApplication) {
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

    public ApplicationSummaryRepresentation getResourceSummary() {
        return resourceSummary;
    }

    public void setResourceSummary(ApplicationSummaryRepresentation resourceSummary) {
        this.resourceSummary = resourceSummary;
    }

    public List<AdvertRepresentationExtended> getRecommendedAdverts() {
        return recommendedAdverts;
    }

    public void setRecommendedAdverts(List<AdvertRepresentationExtended> recommendedAdverts) {
        this.recommendedAdverts = recommendedAdverts;
    }

}
