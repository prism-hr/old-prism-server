package com.zuehlke.pgadmissions.domain;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

public abstract class PrismResourceTransient extends PrismResource {

    public abstract State getPreviousState();

    public abstract void setPreviousState(State previousState);
    
    public abstract LocalDate getDueDate();

    public abstract void setDueDate(LocalDate dueDate);
    
    public abstract String getCode();
    
    public abstract void setCode(String code);
    
    public abstract DateTime getCreatedTimestamp();
    
    public abstract void setCreatedTimestamp(DateTime createdTimestamp);
    
    public abstract String getCodePrefix();
    
    public void generateCode() {
        setCode(getCodePrefix() + "-" + getCreatedTimestamp().getYear() + "-" + String.format("%010d", getId()));
    }    
    
}
