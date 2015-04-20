package com.zuehlke.pgadmissions.domain.workflow;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.INSTITUTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.SYSTEM;

import com.zuehlke.pgadmissions.domain.definitions.PrismLocale;
import com.zuehlke.pgadmissions.domain.definitions.PrismAdvertType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;

public abstract class WorkflowConfiguration extends WorkflowResource {

    public abstract PrismLocale getLocale();

    public abstract void setLocale(PrismLocale locale);

    public abstract PrismAdvertType getAdvertType();

    public abstract void setAdvertType(PrismAdvertType advertType);

    public abstract Boolean getSystemDefault();

    public abstract void setSystemDefault(Boolean systemDefault);

    public abstract WorkflowDefinition getDefinition();

    @Override
    public ResourceSignature getResourceSignature() {
        ResourceSignature resourceSignature = super.getResourceSignature();
        PrismScope resourceScope = getResource().getResourceScope();
        if (resourceScope == SYSTEM) {
            resourceSignature.addProperty("locale", getLocale()).addProperty("advertType", getAdvertType());
        } else if (resourceScope == INSTITUTION) {
            resourceSignature.addProperty("advertType", getAdvertType());
        }
        return resourceSignature;
    }

}
