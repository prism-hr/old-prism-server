package com.zuehlke.pgadmissions.rest.representation.workflow;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;

public class RoleRepresentation {

    private PrismRole id;

    private boolean directlyAssignable;

    public RoleRepresentation(PrismRole id, boolean directlyAssignable) {
        this.id = id;
        this.directlyAssignable = directlyAssignable;
    }

    public PrismRole getId() {
        return id;
    }

    public void setId(PrismRole id) {
        this.id = id;
    }

    public boolean isDirectlyAssignable() {
        return directlyAssignable;
    }

    public void setDirectlyAssignable(boolean directlyAssignable) {
        this.directlyAssignable = directlyAssignable;
    }

}
