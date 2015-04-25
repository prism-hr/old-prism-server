package com.zuehlke.pgadmissions.domain.workflow;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.INSTITUTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.SYSTEM;

import java.util.Arrays;

import org.apache.commons.lang3.ObjectUtils;

import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityType;
import com.zuehlke.pgadmissions.domain.project.Project;
import com.zuehlke.pgadmissions.domain.resource.Resource;

public abstract class WorkflowConfiguration extends WorkflowResource {
    
    public abstract Project getProject();
    
    public abstract void setProject(Project project);

    public abstract PrismOpportunityType getOpportunityType();

    public abstract void setOpportunityType(PrismOpportunityType opportunityType);

    public abstract Boolean getSystemDefault();

    public abstract void setSystemDefault(Boolean systemDefault);

    public abstract WorkflowDefinition getDefinition();
    
    @Override
    public Resource getResource() {
        return ObjectUtils.firstNonNull(super.getResource(), getProject());
    }

    @Override
    public ResourceSignature getResourceSignature() {
        ResourceSignature resourceSignature = super.getResourceSignature();
        if (Arrays.asList(SYSTEM, INSTITUTION).contains(getResource().getResourceScope())) {
            resourceSignature.addProperty("opportunityType", getOpportunityType());
        }
        return resourceSignature;
    }

}
