package com.zuehlke.pgadmissions.rest.representation.resource.application;

import java.util.List;

import com.zuehlke.pgadmissions.rest.representation.user.UserRepresentationSimple;

public class ApplicationRepresentationExtended extends ApplicationRepresentationSimple {

    private List<UserRepresentationSimple> refereesNotResponded;
    
    private List<ApplicationAssignedHiringManagerRepresentation> assignedSupervisors;
    
    private ApplicationOfferRepresentation offerRecommendation;
    
    public List<UserRepresentationSimple> getRefereesNotResponded() {
        return refereesNotResponded;
    }

    public void setRefereesNotResponded(List<UserRepresentationSimple> refereesNotResponded) {
        this.refereesNotResponded = refereesNotResponded;
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
