package com.zuehlke.pgadmissions.domain;

public abstract class WorkflowResourceConfiguration extends WorkflowResource {

    public abstract Boolean getLocked();
    
    public abstract void setLocked(Boolean locked);
    
}
