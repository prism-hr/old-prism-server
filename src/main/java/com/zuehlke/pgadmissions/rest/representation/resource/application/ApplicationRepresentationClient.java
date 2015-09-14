package com.zuehlke.pgadmissions.rest.representation.resource.application;

import java.util.List;

import com.zuehlke.pgadmissions.rest.representation.advert.AdvertRepresentationExtended;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationSimple;
import com.zuehlke.pgadmissions.rest.representation.user.UserRepresentationSimple;

import uk.co.alumeni.prism.api.model.imported.response.ImportedEntityResponse;

public class ApplicationRepresentationClient extends ApplicationRepresentationExtended {

    private List<ImportedEntityResponse> possibleStudyOptions;

    private List<String> possibleLocations;

    private List<UserRepresentationSimple> usersInterestedInApplication;

    private List<UserRepresentationSimple> usersPotentiallyInterestedInApplication;

    private ApplicationInterviewRepresentation interview;

    private Integer referenceProvidedCount;

    private Integer referenceDeclinedCount;

    private List<ResourceRepresentationSimple> otherLiveApplications;

    private List<AdvertRepresentationExtended> recommendedAdverts;

    public List<ImportedEntityResponse> getPossibleStudyOptions() {
        return possibleStudyOptions;
    }

    public void setPossibleStudyOptions(List<ImportedEntityResponse> possibleStudyOptions) {
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

    public Integer getReferenceProvidedCount() {
        return referenceProvidedCount;
    }

    public void setReferenceProvidedCount(Integer referenceProvidedCount) {
        this.referenceProvidedCount = referenceProvidedCount;
    }

    public Integer getReferenceDeclinedCount() {
        return referenceDeclinedCount;
    }

    public void setReferenceDeclinedCount(Integer referenceDeclinedCount) {
        this.referenceDeclinedCount = referenceDeclinedCount;
    }

    public List<ResourceRepresentationSimple> getOtherLiveApplications() {
        return otherLiveApplications;
    }

    public void setOtherLiveApplications(List<ResourceRepresentationSimple> otherLiveApplications) {
        this.otherLiveApplications = otherLiveApplications;
    }

    public List<AdvertRepresentationExtended> getRecommendedAdverts() {
        return recommendedAdverts;
    }

    public void setRecommendedAdverts(List<AdvertRepresentationExtended> recommendedAdverts) {
        this.recommendedAdverts = recommendedAdverts;
    }

}
