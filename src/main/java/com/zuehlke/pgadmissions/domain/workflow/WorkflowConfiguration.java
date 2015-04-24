package com.zuehlke.pgadmissions.domain.workflow;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.INSTITUTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.SYSTEM;

import java.util.Arrays;

import com.zuehlke.pgadmissions.domain.definitions.PrismProgramType;

public abstract class WorkflowConfiguration extends WorkflowResource {

    public abstract PrismProgramType getProgramType();

    public abstract void setProgramType(PrismProgramType programType);

    public abstract Boolean getSystemDefault();

    public abstract void setSystemDefault(Boolean systemDefault);

    public abstract WorkflowDefinition getDefinition();

    @Override
    public ResourceSignature getResourceSignature() {
        ResourceSignature resourceSignature = super.getResourceSignature();
        if (Arrays.asList(SYSTEM, INSTITUTION).contains(getResource().getResourceScope())) {
            resourceSignature.addProperty("programType", getProgramType());
        }
        return resourceSignature;
    }

}
