package com.zuehlke.pgadmissions.domain.definitions.workflow;

public enum PrismActionType {
    
    USER_INVOCATION(false), //
    SYSTEM_ESCALATION(true), //
    SYSTEM_IMPORT(true), //
    SYSTEM_PROPAGATION(true);

    private boolean systemAction;
    
    private PrismActionType(boolean systemAction) {
        this.systemAction = systemAction;
    }

    public boolean isSystemAction() {
        return systemAction;
    }
    
}
