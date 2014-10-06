package com.zuehlke.pgadmissions.domain;

import org.joda.time.DateTime;

import com.zuehlke.pgadmissions.domain.definitions.PrismLocale;

public abstract class WorkflowResourceVersion {

    public abstract PrismLocale getLocale();
    
    public abstract void setLocale(PrismLocale locale);
    
    public abstract Boolean getActive();
    
    public abstract void setActive(Boolean active);
    
    public abstract DateTime getCreatedTimestamp();
    
    public abstract void setCreatedTimestamp(DateTime createdTimestamp);
    
}
