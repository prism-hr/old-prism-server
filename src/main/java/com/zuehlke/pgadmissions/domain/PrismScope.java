package com.zuehlke.pgadmissions.domain;

public interface PrismScope {
    
    public Integer getId();

    public String getScopeName();
    
    public PrismSystem getSystem();

    public Institution getInstitution();
    
    public Program getProgram();

    public Project getProject();
    
    public State getState();
    
    public void setState(State state);
    
}
