package com.zuehlke.pgadmissions.rest.representation.resource.application;

import java.util.List;

public class ApplicationRepresentationExtended extends ApplicationRepresentationSimple {

    private List<ApplicationAssignedHiringManagerRepresentation> assignedSupervisors;
    
    private ApplicationOfferRepresentation offerRecommendation;
    
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
