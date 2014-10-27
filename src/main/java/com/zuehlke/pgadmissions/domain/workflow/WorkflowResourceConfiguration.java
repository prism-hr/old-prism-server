package com.zuehlke.pgadmissions.domain.workflow;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.INSTITUTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.SYSTEM;

import com.zuehlke.pgadmissions.domain.definitions.PrismLocale;
import com.zuehlke.pgadmissions.domain.definitions.PrismProgramType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;

public abstract class WorkflowResourceConfiguration extends WorkflowResource {

    public abstract PrismProgramType getProgramType();

    public abstract void setProgramType(PrismProgramType programType);

    public abstract PrismLocale getLocale();

    public abstract void setLocale(PrismLocale locale);

    public abstract Boolean getSystemDefault();

    public abstract void setSystemDefault(Boolean systemDefault);

    @Override
    public ResourceSignature getResourceSignature() {
        ResourceSignature resourceSignature = super.getResourceSignature();
        PrismScope resourceScope = getResource().getResourceScope();
        if (resourceScope == SYSTEM) {
            resourceSignature.addProperty("locale", getLocale()).addProperty("programType", getProgramType());
        } else if (resourceScope == INSTITUTION) {
            resourceSignature.addProperty("programType", getProgramType());
        }
        return resourceSignature;
    }

}
