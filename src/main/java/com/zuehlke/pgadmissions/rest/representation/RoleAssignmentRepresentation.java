package com.zuehlke.pgadmissions.rest.representation;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;

public class RoleAssignmentRepresentation {

    private PrismRole id;

    private Boolean value;

    public RoleAssignmentRepresentation(PrismRole id, Boolean value) {
        this.id = id;
        this.value = value;
    }

    public PrismRole getId() {
        return id;
    }

    public void setId(PrismRole id) {
        this.id = id;
    }

    public Boolean getValue() {
        return value;
    }

    public void setValue(Boolean value) {
        this.value = value;
    }
    
}
