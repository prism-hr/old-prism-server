package com.zuehlke.pgadmissions.rest.representation.resource.application;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.rest.representation.user.UserRepresentationSimple;

public class ApplicationAssignedHiringManagerRepresentation {

    private UserRepresentationSimple user;

    private PrismRole role;

    private Boolean approvedAppointment;

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

    public final Boolean getApprovedAppointment() {
        return approvedAppointment;
    }

    public final void setApprovedAppointment(Boolean approvedAppointment) {
        this.approvedAppointment = approvedAppointment;
    }

    public ApplicationAssignedHiringManagerRepresentation withUser(UserRepresentationSimple user) {
        this.user = user;
        return this;
    }

    public ApplicationAssignedHiringManagerRepresentation withRole(PrismRole role) {
        this.role = role;
        return this;
    }

    public ApplicationAssignedHiringManagerRepresentation withApprovedAppointment(Boolean approvedAppointment) {
        this.approvedAppointment = approvedAppointment;
        return this;
    }

}
