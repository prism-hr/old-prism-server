package com.zuehlke.pgadmissions.domain;

import java.util.Map;

import com.zuehlke.pgadmissions.domain.definitions.PrismLocale;

public abstract class WorkflowResourceLocalized <T extends WorkflowResourceVersion> extends WorkflowResource {

    public abstract Map<PrismLocale, T> getVersions();
    
    public T getVersion(PrismLocale locale) {
        return getVersions().get(locale);
    }
    
    public void addVersion(PrismLocale locale, T version) {
        getVersions().put(locale, version);
    }

}
