package com.zuehlke.pgadmissions.domain;

import org.apache.commons.beanutils.PropertyUtils;

public abstract class PrismScope {
    
    public abstract Integer getId();

    public abstract String getScopeName();
    
    public abstract PrismSystem getSystem();

    public abstract Institution getInstitution();
    
    public abstract Program getProgram();

    public abstract Project getProject();
    
    public abstract ApplicationForm getApplication();
    
    public abstract State getState();
    
    public abstract void setState(State state);
    
    public PrismScope getEnclosingScope(String scopeName) {
        try {
            return (PrismScope) PropertyUtils.getSimpleProperty(this, scopeName);
        } catch (Exception e) {
            throw new Error(e);
        }
    }
    
}
