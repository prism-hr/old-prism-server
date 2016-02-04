package com.zuehlke.pgadmissions.domain.workflow;

public abstract class WorkflowConfigurationVersioned extends WorkflowConfiguration {

    public abstract Boolean getActive();
    
    public abstract void setActive(Boolean active);
    
    public abstract Integer getVersion();

    public abstract void setVersion(Integer version);

    @Override
    public ResourceSignature getResourceSignature() {
        return super.getResourceSignature().addProperty("version", getVersion());
    }

}
