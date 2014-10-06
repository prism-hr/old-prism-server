package com.zuehlke.pgadmissions.domain;

import com.zuehlke.pgadmissions.domain.definitions.PrismLocale;

public abstract class WorkflowResourceVersion implements IUniqueEntity {

    public abstract PrismLocale getLocale();
    
    public abstract void setLocale(PrismLocale locale);
    
    @Override
    public ResourceSignature getResourceSignature() {
        return new ResourceSignature().addProperty("locale", getLocale());
    }
    
}
