package com.zuehlke.pgadmissions.rest.representation.resource.application;

import java.util.List;

public class ApplicationRepresentationExtended extends ApplicationRepresentationSimple {

    private List<ApplicationAssignedSupervisorRepresentation> assignedSupervisors;
    
    private ApplicationOfferRepresentation offerRecommendation;
    
    public List<ApplicationAssignedSupervisorRepresentation> getAssignedSupervisors() {
        return assignedSupervisors;
    }

    public void setAssignedSupervisors(List<ApplicationAssignedSupervisorRepresentation> assignedSupervisors) {
        this.assignedSupervisors = assignedSupervisors;
    }

    public ApplicationOfferRepresentation getOfferRecommendation() {
        return offerRecommendation;
    }

    public void setOfferRecommendation(ApplicationOfferRepresentation offerRecommendation) {
        this.offerRecommendation = offerRecommendation;
    }
    
}
