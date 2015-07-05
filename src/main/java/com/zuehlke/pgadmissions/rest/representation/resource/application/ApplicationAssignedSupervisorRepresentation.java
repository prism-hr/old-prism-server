package com.zuehlke.pgadmissions.rest.representation.resource.application;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.rest.representation.user.UserRepresentationSimple;

public class ApplicationAssignedSupervisorRepresentation {

    private UserRepresentationSimple user;

    private PrismRole role;

    private Boolean acceptedSupervision;

    public final UserRepresentationSimple getUser() {
        return user;
    }

    public final void setUser(UserRepresentationSimple user) {
        this.user = user;
    }

    public final PrismRole getRole() {
        return role;
    }

    public final void setRole(PrismRole role) {
        this.role = role;
    }

    public final Boolean getAcceptedSupervision() {
        return acceptedSupervision;
    }

    public final void setAcceptedSupervision(Boolean acceptedSupervision) {
        this.acceptedSupervision = acceptedSupervision;
    }

    public ApplicationAssignedSupervisorRepresentation withUser(UserRepresentationSimple user) {
        this.user = user;
        return this;
    }

    public ApplicationAssignedSupervisorRepresentation withRole(PrismRole role) {
        this.role = role;
        return this;
    }

    public ApplicationAssignedSupervisorRepresentation withAcceptedSupervision(Boolean acceptedSupervision) {
        this.acceptedSupervision = acceptedSupervision;
        return this;
    }

}
