package com.zuehlke.pgadmissions.domain;

import org.joda.time.LocalDate;

public abstract class PrismResourceTransient extends PrismResource {

    public abstract State getPreviousState();

    public abstract void setPreviousState(State previousState);
    
    public abstract LocalDate getDueDate();

    public abstract void setDueDate(LocalDate dueDate);
    
    public abstract String getCodePrefix();
    
}
