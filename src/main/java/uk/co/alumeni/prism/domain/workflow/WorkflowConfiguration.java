package uk.co.alumeni.prism.domain.workflow;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismScope;

import java.util.Arrays;

public abstract class WorkflowConfiguration<T> extends WorkflowResource {

    public abstract OpportunityType getOpportunityType();

    public abstract void setOpportunityType(OpportunityType opportunityType);

    public abstract T getDefinition();

    public abstract void setDefinition(T definition);

    public abstract Boolean getSystemDefault();

    public abstract void setSystemDefault(Boolean systemDefault);

    @Override
    public EntitySignature getEntitySignature() {
        EntitySignature entitySignature = super.getEntitySignature();
        if (Arrays.asList(PrismScope.SYSTEM, PrismScope.INSTITUTION, PrismScope.DEPARTMENT).contains(getResource().getResourceScope())) {
            entitySignature.addProperty("opportunityType", getOpportunityType());
        }
        return entitySignature.addProperty("definition", getDefinition());
    }

}
