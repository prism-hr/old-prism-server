package com.zuehlke.pgadmissions.domain.definitions.workflow;

public enum PrismRoleTransitionType {

    DELETE(true), //
    RETIRE(false), //
    CREATE(true), //
    BRANCH(false), //
    UPDATE(false);
    
    private boolean specified;

    private PrismRoleTransitionType(boolean specified) {
        this.specified = specified;
    }

    public final boolean isSpecified() {
        return specified;
    }
    
}
