package com.zuehlke.pgadmissions.domain;

public abstract class WorkflowResourceConfiguration implements IUniqueEntity {

    public abstract System getSystem();
    
    public abstract Institution getInstitution();
    
    public abstract Program getProgram();
    
    public abstract void setSystem(System system);
    
    public abstract void setInstitution(Institution institution);
    
    public abstract void setProgram(Program program);

}
