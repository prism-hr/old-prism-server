package uk.co.alumeni.prism.rest.representation.resource.application;

import java.util.List;

import uk.co.alumeni.prism.rest.representation.user.UserRepresentationSimple;

public class ApplicationRepresentationExtended extends ApplicationRepresentationSimple {

    private List<UserRepresentationSimple> refereesWithoutReference;

    private List<ApplicationAssignedHiringManagerRepresentation> assignedSupervisors;

    private ApplicationOfferRepresentation offerRecommendation;

    public List<UserRepresentationSimple> getRefereesWithoutReference() {
        return refereesWithoutReference;
    }

    public void setWithoutReference(List<UserRepresentationSimple> refereesWithoutReference) {
        this.refereesWithoutReference = refereesWithoutReference;
    }

    public List<ApplicationAssignedHiringManagerRepresentation> getAssignedSupervisors() {
        return assignedSupervisors;
    }

    public void setAssignedSupervisors(List<ApplicationAssignedHiringManagerRepresentation> assignedSupervisors) {
        this.assignedSupervisors = assignedSupervisors;
    }

    public ApplicationOfferRepresentation getOfferRecommendation() {
        return offerRecommendation;
    }

    public void setOfferRecommendation(ApplicationOfferRepresentation offerRecommendation) {
        this.offerRecommendation = offerRecommendation;
    }

}
