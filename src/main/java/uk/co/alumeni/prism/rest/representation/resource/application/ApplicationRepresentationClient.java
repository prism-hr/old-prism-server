package uk.co.alumeni.prism.rest.representation.resource.application;

import java.util.List;

import uk.co.alumeni.prism.domain.definitions.PrismStudyOption;
import uk.co.alumeni.prism.rest.representation.advert.AdvertCompetenceRepresentation;
import uk.co.alumeni.prism.rest.representation.advert.AdvertThemeRepresentation;
import uk.co.alumeni.prism.rest.representation.resource.ResourceRepresentationLocationRelation;
import uk.co.alumeni.prism.rest.representation.user.UserRepresentationSimple;

public class ApplicationRepresentationClient extends ApplicationRepresentationExtended {

    private List<PrismStudyOption> possibleStudyOptions;

    private List<AdvertThemeRepresentation> possibleThemes;

    private List<ResourceRepresentationLocationRelation> possibleLocations;

    private List<UserRepresentationSimple> usersInterestedInApplication;

    private List<UserRepresentationSimple> usersPotentiallyInterestedInApplication;

    private ApplicationInterviewRepresentation interview;

    private List<AdvertCompetenceRepresentation> competences;

    public List<PrismStudyOption> getPossibleStudyOptions() {
        return possibleStudyOptions;
    }

    public void setPossibleStudyOptions(List<PrismStudyOption> possibleStudyOptions) {
        this.possibleStudyOptions = possibleStudyOptions;
    }

    public List<AdvertThemeRepresentation> getPossibleThemes() {
        return possibleThemes;
    }

    public void setPossibleThemes(List<AdvertThemeRepresentation> possibleThemes) {
        this.possibleThemes = possibleThemes;
    }

    public List<ResourceRepresentationLocationRelation> getPossibleLocations() {
        return possibleLocations;
    }

    public void setPossibleLocations(List<ResourceRepresentationLocationRelation> possibleLocations) {
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

    public List<AdvertCompetenceRepresentation> getCompetences() {
        return competences;
    }

    public void setCompetences(List<AdvertCompetenceRepresentation> competences) {
        this.competences = competences;
    }
}
