package com.zuehlke.pgadmissions.rest.representation.workflow;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;

public class RoleRepresentation {

    private PrismRole id;

    private Boolean verified;

    private Boolean directlyAssignable;

    public RoleRepresentation(PrismRole id, Boolean verified) {
        this.id = id;
        this.verified = verified;
    }

    public RoleRepresentation(PrismRole id, Boolean verified, Boolean directlyAssignable) {
        this(id, verified);
        this.directlyAssignable = directlyAssignable;
    }

    public PrismRole getId() {
        return id;
    }

    public void setId(PrismRole id) {
        this.id = id;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public boolean isDirectlyAssignable() {
        return directlyAssignable;
    }

    public void setDirectlyAssignable(Boolean directlyAssignable) {
        this.directlyAssignable = directlyAssignable;
    }

}
