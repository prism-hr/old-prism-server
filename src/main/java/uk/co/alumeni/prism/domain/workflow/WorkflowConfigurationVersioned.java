package uk.co.alumeni.prism.domain.workflow;

public abstract class WorkflowConfigurationVersioned<T> extends WorkflowConfiguration<T> {

    public abstract Boolean getActive();

    public abstract void setActive(Boolean active);

    public abstract Integer getVersion();

    public abstract void setVersion(Integer version);

    @Override
    public EntitySignature getEntitySignature() {
        return super.getEntitySignature().addProperty("version", getVersion());
    }

}
