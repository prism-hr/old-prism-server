package com.zuehlke.pgadmissions.domain.workflow;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.DEPARTMENT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.INSTITUTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.SYSTEM;

import java.util.Arrays;

import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityType;

public abstract class WorkflowConfiguration<T> extends WorkflowResource {

    public abstract PrismOpportunityType getOpportunityType();

    public abstract void setOpportunityType(PrismOpportunityType opportunityType);

    public abstract T getDefinition();

    public abstract void setDefinition(T definition);

    public abstract Boolean getSystemDefault();

    public abstract void setSystemDefault(Boolean systemDefault);

    @Override
    public ResourceSignature getResourceSignature() {
        ResourceSignature resourceSignature = super.getResourceSignature();
        if (Arrays.asList(SYSTEM, INSTITUTION, DEPARTMENT).contains(getResource().getResourceScope())) {
            resourceSignature.addProperty("opportunityType", getOpportunityType());
        }
        return resourceSignature.addProperty("definition", getDefinition());
    }

}
