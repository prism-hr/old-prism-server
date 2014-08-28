package com.zuehlke.pgadmissions.domain;

import org.joda.time.LocalDate;

public abstract class RecruiterResource extends ParentResource {
    
    public abstract LocalDate getRecommendedStartDate();

}
