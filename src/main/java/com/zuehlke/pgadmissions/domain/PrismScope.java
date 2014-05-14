package com.zuehlke.pgadmissions.domain;

public interface PrismScope {

    public String getScopeName();
    
    public PrismSystem getSystem();

    public Institution getInstitution();
    
    public Program getProgram();

    public Project getProject();
    
}
